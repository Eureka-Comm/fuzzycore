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
import com.castellanos94.jfuzzylogic.core.base.impl.Generator;

/**
 * Class for Operator utilities
 * 
 * @see Operator
 */
public class OperatorUtil {
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
     * @exception JFuzzyLogicError anything related to
     *                             {@code root.getClass().getDeclaredConstructor().newInstance() }
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
                    if (e instanceof Operator) {
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
}
