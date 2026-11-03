package com.threeclassics.backgammon;

import com.threeclassics.backgammon.BackgammonMove.Step;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Every legal way to play a roll, as a graph the client can walk one step at a time.
 *
 * <p>Node 0 is the start of the turn. Each node is a distinct intermediate position (with the dice
 * still unused), so the many orders in which doubles can be played share nodes instead of being
 * listed one by one. Every path from node 0 to a {@code complete} node is a legal turn.
 *
 * @param nodes each node's outgoing steps as {@code [from, to, die, hit (0/1), child]}
 */
public record TurnGraph(List<Node> nodes) {

    public record Node(List<int[]> steps, boolean complete) {
    }

    static TurnGraph of(Position start, List<List<Step>> sequences) {
        List<List<int[]>> edges = new ArrayList<>();
        List<Boolean> complete = new ArrayList<>();
        Map<String, Integer> ids = new HashMap<>();
        edges.add(new ArrayList<>());
        complete.add(false);

        for (List<Step> sequence : sequences) {
            Position position = start.copy();
            int node = 0;
            List<Integer> used = new ArrayList<>();
            for (int i = 0; i < sequence.size(); i++) {
                Step step = sequence.get(i);
                position.apply(step);
                used.add(step.die());
                int[] sortedUsed = used.stream().mapToInt(Integer::intValue).sorted().toArray();
                String key = position.key() + Arrays.toString(sortedUsed);
                Integer child = ids.get(key);
                if (child == null) {
                    child = edges.size();
                    ids.put(key, child);
                    edges.add(new ArrayList<>());
                    complete.add(i == sequence.size() - 1);
                }
                int target = child;
                boolean known = edges.get(node).stream().anyMatch(e -> e[0] == step.from() && e[1] == step.to() && e[2] == step.die());
                if (!known) {
                    edges.get(node).add(new int[] {step.from(), step.to(), step.die(), step.hit() ? 1 : 0, target});
                }
                node = target;
            }
        }

        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            nodes.add(new Node(edges.get(i), complete.get(i)));
        }
        return new TurnGraph(nodes);
    }
}
