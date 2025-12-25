package main.ast;

import main.util.Comment;
import main.util.Span;
import main.visit.NodeVisitor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public abstract class Node {

    private Node parent;                 // null for root
    private final Span span;
    private final NodeKind kind;

    private final List<Comment> leadingComments;
    private final List<Comment> trailingComments;

    protected Node(NodeKind kind,
                   Span span,
                   List<Comment> leadingComments,
                   List<Comment> trailingComments) {
        this.kind = Objects.requireNonNull(kind, "kind");
        this.span = Objects.requireNonNull(span, "span");
        this.leadingComments  = leadingComments  != null ? List.copyOf(leadingComments)  : List.of();
        this.trailingComments = trailingComments != null ? List.copyOf(trailingComments) : List.of();
    }

    protected Node(NodeKind kind) {
        this(kind, new Span(), List.of(), List.of());
    }

    // ----------------------------------------------------------------------
    // Basic properties
    // ----------------------------------------------------------------------

    public NodeKind getKind()      { return kind; }
    public Span getSpan()          { return span; }
    public Node getParent()        { return parent; }
    public boolean hasParent()     { return parent != null; }

    public void setParent(Node parent) {   // package-private: only AST building code calls this
        this.parent = parent;
    }

    public List<Comment> getLeadingComments()  { return leadingComments; }
    public List<Comment> getTrailingComments() { return trailingComments; }

    // Implemented by subclasses
    public abstract List<Node> getChildren();
    public abstract <R> R accept(NodeVisitor<R> nodeVisitor);

    // ----------------------------------------------------------------------
    // Child attachment helpers (used in constructors of concrete nodes)
    // ----------------------------------------------------------------------

    protected <T extends Node> T adoptChild(T child) {
        if (child != null) {
            child.setParent(this);
        }
        return child;
    }

    protected <T extends Node> List<T> adoptChildren(List<T> children) {
        if (children == null || children.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> copy = new ArrayList<>(children.size());
        for (T child : children) {
            if (child != null) {
                child.setParent(this);
                copy.add(child);
            }
        }
        return Collections.unmodifiableList(copy);
    }

    protected <T extends Node> List<T> adoptAll(List<T> children) {
        if (children == null || children.isEmpty()) return List.of();
        for (T child : children)
            if (child != null) child.setParent(this);
        return List.copyOf(children);
    }

    // ----------------------------------------------------------------------
    // Children & siblings
    // ----------------------------------------------------------------------

    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    public int getChildCount() {
        return getChildren().size();
    }

    public Node getChild(int index) {
        return getChildren().get(index);
    }

    /**
     * Index of this node in its parent's children list, or -1 if no parent.
     */
    public int getIndexInParent() {
        if (parent == null) return -1;
        List<Node> siblings = parent.getChildren();
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i) == this) {
                return i;
            }
        }
        return -1;
    }

    public Node getPreviousSibling() {
        if (parent == null) return null;
        List<Node> siblings = parent.getChildren();
        int idx = getIndexInParent();
        if (idx <= 0) return null;
        return siblings.get(idx - 1);
    }

    public Node getNextSibling() {
        if (parent == null) return null;
        List<Node> siblings = parent.getChildren();
        int idx = getIndexInParent();
        if (idx == -1 || idx + 1 >= siblings.size()) return null;
        return siblings.get(idx + 1);
    }

    // ----------------------------------------------------------------------
    // Ancestors
    // ----------------------------------------------------------------------

    /**
     * Returns all ancestors of this node, starting from the parent up to the root.
     */
    public List<Node> getAncestors() {
        List<Node> result = new ArrayList<>();
        Node current = this.parent;
        while (current != null) {
            result.add(current);
            current = current.parent;
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the root of the tree that contains this node.
     */
    public Node getRoot() {
        Node current = this;
        while (current.parent != null) {
            current = current.parent;
        }
        return current;
    }

    /**
     * Returns the nearest ancestor of the given type, or null if none.
     */
    public <T extends Node> T getAncestorOfType(Class<T> type) {
        Node current = this.parent;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.parent;
        }
        return null;
    }

    // ----------------------------------------------------------------------
    // Descendants
    // ----------------------------------------------------------------------

    /**
     * All descendants of this node (children, grandchildren, etc), depth-first.
     */
    public List<Node> getDescendants() {
        List<Node> result = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>(getChildren());
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            result.add(n);
            // Push children in reverse so natural left-to-right order is preserved.
            List<Node> children = n.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * This node plus all descendants (preorder traversal).
     */
    public List<Node> getDescendantsAndSelf() {
        List<Node> result = new ArrayList<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(this);
        while (!stack.isEmpty()) {
            Node n = stack.pop();
            result.add(n);
            List<Node> children = n.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * All descendants of a given type.
     */
    public <T extends Node> List<T> getDescendantsOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Node n : getDescendants()) {
            if (type.isInstance(n)) {
                result.add(type.cast(n));
            }
        }
        return Collections.unmodifiableList(result);
    }


}
