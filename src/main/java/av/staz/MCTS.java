package av.staz;

import java.util.*;

public class MCTS {
    static int MAXDEPTH = 1; // 1, bo robimy płytkie drzewo
    static int ITERATIONS = 1000; // Sensowna liczba iteracji, w zasadzie >100 wystarczy
    static int ROLLOUT = 10; // Ile do przodu robimy każdą z symulacji, tu 10 kroków

    static class Node {
        State state;
        Node parent;
        List<Node> children;
        int visits;
        double wins;
        int depth;

        Node(State state, Node parent, int depth) {
            this.state = state;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.visits = 0;
            this.wins = 0;
            this.depth = depth;
        }

        boolean isFullyExpanded() {
            return children.size() == state.getAvailableMoves().size();
        }

        Node bestChild(double c) {
            Node best = null;
            double bestValue = Double.NEGATIVE_INFINITY;

            for (Node child : children) {
                double uctValue = (child.wins / (child.visits + 1e-6)) +
                        c * Math.sqrt(Math.log(this.visits + 1) / (child.visits + 1e-6));
                if (uctValue > bestValue) {
                    bestValue = uctValue;
                    best = child;
                }
            }
            return best;
        }
    }

    public static State findNextMove(State rootState) {
        Node root = new Node(rootState, null, 0);

        for (int i = 0; i < ITERATIONS; i++) {
            Node node = treePolicy(root);
            double reward = defaultPolicy(node.state);
            backpropagate(node, reward);
        }

        Node bestChild = Collections.max(root.children, Comparator.comparingInt(n -> n.visits));
        return bestChild.state;
    }

    private static Node treePolicy(Node node) {
        while (node.depth < MAXDEPTH) {
            if (!node.isFullyExpanded()) {
                node = expand(node);
            }

            node = node.bestChild(Math.sqrt(2));
        }
        return node;
    }

    private static Node expand(Node node) {
        List<State> possibleMoves = node.state.getAvailableMoves();

        Set<State> triedMoves = new HashSet<>();
        for (Node child : node.children) {
            triedMoves.add(child.state);
        }

        for (State move : possibleMoves) {
            if (!triedMoves.contains(move)) {
                Node childNode = new Node(move, node, node.depth + 1);
                node.children.add(childNode);
            }
        }
        return node;
    }

    private static double defaultPolicy(State state) {
        Random random = new Random();
        int i = 0;
        int numGreen = state.greenLights().size();
        int north = state.north();
        int south = state.south();
        int east = state.east();
        int west = state.west();
        while (i < ROLLOUT) {
            int countToDecrease = random.nextInt(numGreen) + 1;
            List<Direction> greenCopy = new ArrayList<>(state.greenLights());
            Collections.shuffle(greenCopy, random);
            List<Direction> chosen = greenCopy.subList(0, countToDecrease);
            for (Direction dir : chosen) {
                switch (dir) {
                    case NORTH -> north = Math.max(0, north - 1);
                    case SOUTH -> south = Math.max(0, south - 1);
                    case EAST -> east = Math.max(0, east - 1);
                    case WEST -> west = Math.max(0, west - 1);
                }
            }
            i++;
        }
        return new State(north, south, east, west, state.greenLights()).reward();
    }

    private static void backpropagate(Node node, double reward) {
        while (node != null) {
            node.visits++;
            node.wins += reward;
            node = node.parent;
        }
    }
}
