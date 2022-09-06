package com.castellanos94.jfuzzylogic.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.castellanos94.jfuzzylogic.core.base.AElement;
import com.castellanos94.jfuzzylogic.core.base.JFuzzyLogicError;
import com.castellanos94.jfuzzylogic.core.base.Operator;
import com.castellanos94.jfuzzylogic.core.base.OperatorType;
import com.castellanos94.jfuzzylogic.core.base.impl.And;
import com.castellanos94.jfuzzylogic.core.base.impl.Eqv;
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;
import com.castellanos94.jfuzzylogic.core.base.impl.Imp;
import com.castellanos94.jfuzzylogic.core.base.impl.Not;
import com.castellanos94.jfuzzylogic.core.base.impl.Or;
import com.castellanos94.jfuzzylogic.core.base.impl.State;

/**
 * Class for Operator utilities
 * 
 * @see Operator
 */
public class OperatorUtil {
    /**
     * Obtains the maximum level of the element
     * 
     * @param root
     * @return
     */
    public static int getMaximumLeafLevel(AElement element) {
        if (element instanceof State || element instanceof Generator) {
            return 1;
        }
        Operator root = (Operator) element;
        ArrayList<Operator> operators = getNodesByClass(root, Operator.class);
        if (!operators.isEmpty()) {
            int max = 0;
            for (Operator e : operators) {
                int lvl = getMaximumLeafLevel((Operator) e) + 1;
                max = Math.max(max, lvl);
            }
            return max;
        }
        return 1;
    }

    /**
     * Valide predicate/state
     * 
     * @param element           to evaluate
     * @param includeMembership if its true performance membership validation
     * @return
     */
    public static boolean isValid(AElement element, boolean... includeMembership) {
        if (element == null) {
            return false;
        }
        boolean flag = false;
        OperatorType type = null;
        if (element instanceof State) {
            State state = (State) element;
            flag = state.getColName() != null && state.getLabel() != null;
            if (includeMembership.length > 0 && includeMembership[0]) {
                flag = flag && state.getMembershipFunction() != null
                        && state.getMembershipFunction().isValid();
            }
        } else if (element instanceof Generator) {
            Generator generator = (Generator) element;
            flag = generator.getLabel() != null && generator.getDepth() != null && generator.getStates().size() > 1;
            List<Generator> generators = generator.getGenerators();
            if (!generators.isEmpty()) {
                for (int i = 0; i < generators.size() && flag; i++) {
                    flag = flag
                            && isValid(generators.get(i), includeMembership.length > 0 ? includeMembership[0] : false);
                }
            }
        } else {
            type = getType((Operator) element);
        }

        if (type != null) {
            Operator operator = (Operator) element;
            int count = 0;
            for (AElement ae : operator) {
                boolean av = isValid(ae, includeMembership.length > 0 ? includeMembership[0] : false);
                if (!av) {
                    return false;
                }
                count++;
            }
            switch (type) {
                case AND:
                case OR:
                    flag = count >= 2;
                    break;
                case IMP:
                case EQV:
                    flag = count == 2;
                    break;
                case NOT:
                    flag = count == 1;
                    break;
            }
        }
        return flag;
    }

    /**
     * gets instance based on type
     * 
     * @param type operator
     * @return new instance
     * @throws JFuzzyLogicError if the type is unknown
     */
    public static Operator getInstance(OperatorType type) {
        switch (type) {
            case AND:
                return new And();
            case OR:
                return new Or();
            case IMP:
                return new Imp();
            case EQV:
                return new Eqv();
            case NOT:
                return new Not();
            default:
                throw new JFuzzyLogicError("No class registered for " + type);
        }
    }

    /**
     * Gets the type based on the instance.
     * 
     * @param operator to get type
     * @return If the instance is of type generator it returns null
     * @throws JFuzzyLogicError if the instance is unknown it throws
     *                          exception.
     */
    public static OperatorType getType(Operator operator) {
        if (operator instanceof Generator) {
            return null;
        }
        if (operator instanceof And) {
            return OperatorType.AND;
        }
        if (operator instanceof Or) {
            return OperatorType.OR;
        }
        if (operator instanceof Not) {
            return OperatorType.NOT;
        }
        if (operator instanceof Imp) {
            return OperatorType.IMP;
        }
        if (operator instanceof Eqv) {
            return OperatorType.EQV;
        }
        throw new JFuzzyLogicError("Unknown type for " + operator.getClass().getSimpleName());
    }

    /**
     * Get nivel in the tree by Depth-first search
     * 
     * @param root
     * @param node
     * @return
     */
    public static int dfs(Operator root, AElement node) {
        return dfs(root, node, 1);
    }

    /**
     * Aux function for DFS
     * 
     * @param root
     * @param node
     * @param pos
     * @return
     */
    private static int dfs(Operator root, AElement node, int pos) {
        if (node.equals(root)) {
            return pos;
        }
        for (AElement n : root) {
            if (node.equals(n)) {
                return pos;
            } else if (n instanceof Operator && !(n instanceof Generator)) {
                int position = dfs((Operator) n, node, pos + 1);
                if (position != -1) {
                    return position;
                }
            }
        }
        return -1;
    }

    /**
     * Obtains all editable nodes belonging to the operator and their successors
     * 
     * @param operator root
     * @return editable nodes
     */
    public static List<AElement> getEditableNode(Operator operator) {
        ArrayList<AElement> all = getNodesByClass(operator, AElement.class);
        return all.stream().filter(element -> element.isEditable()).collect(Collectors.toList());
    }

    /**
     * Obtains all nodes per class belonging to the operator and their successors
     * 
     * @param <T>   parameterized class
     * @param tree  root
     * @param clazz class
     * @return nodes of class from root
     */
    public static <T> ArrayList<T> getNodesByClass(Operator tree, Class<T> clazz) {
        ArrayList<T> nodes = new ArrayList<>();
        if (clazz == Generator.class) {
            if (clazz.isInstance(tree)) {
                nodes.add(clazz.cast(tree));
            }
        }
        _getNodesByClass(tree, nodes, clazz);
        return nodes;
    }

    /**
     * It replaces the old value with a new one, this causes it to iterate through
     * all the elements of the tree and generates a new tree with the replacement if
     * any. This is a silly copy.
     * 
     * @param root     operator
     * @param oldValue to replace
     * @param newValue to set
     * @return silly copy
     * @throws JFuzzyLogicError anything related to
     *                          {@code root.getClass().getDeclaredConstructor().newInstance() }
     */
    public static Operator replace(final Operator root, final AElement oldValue, final AElement newValue) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        Iterator<AElement> child = root.iterator();
        Operator nr;
        try {
            nr = root.getClass().getDeclaredConstructor().newInstance();
            while (child.hasNext()) {
                AElement e = child.next();
                if (e.equals(oldValue)) {
                    nr.add(newValue);
                } else {
                    if (e instanceof Operator && !(e instanceof Generator)) {
                        nr.add(replace((Operator) e, oldValue, newValue));
                    } else {
                        nr.add(e);
                    }
                }

            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e1) {
            throw new JFuzzyLogicError("Replace " + e1.getMessage());
        }

        return nr;
    }

    private static <T> void _getNodesByClass(Operator tree, ArrayList<T> nodes, Class<T> clazz) {
        boolean isOperator = clazz == Operator.class;
        for (AElement element : tree) {
            if (clazz.isInstance(element)) {
                if (isOperator && !(element instanceof Generator)) {
                    nodes.add(clazz.cast(element));
                } else if (!isOperator) {
                    nodes.add(clazz.cast(element));
                }
            }
            if (element instanceof Operator && !(element instanceof Generator)) {
                _getNodesByClass((Operator) element, nodes, clazz);
            }
        }
    }

    /**
     * Gets the parent node, if it exists in the operator.
     * 
     * @param root operator to be searched
     * @param node child node
     * @return If the father exists, otherwise null
     */
    public static Operator getRoot(Operator root, AElement node) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(node);
        for (AElement a : root) {
            if (a.equals(node)) {
                return root;
            } else if (a instanceof Operator && !(a instanceof Generator)) {
                Operator tmp = getRoot((Operator) a, node);
                if (tmp != null) {
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * Obtains successors
     * 
     * @param root parent
     * @return direct child
     */
    public static List<AElement> getSuccessors(Operator root) {
        Objects.requireNonNull(root);
        List<AElement> elements = new ArrayList<>();
        for (AElement e : root) {
            elements.add(e);
        }
        return elements;
    }
}
