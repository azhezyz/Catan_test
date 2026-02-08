package catan;

public final class ActionDecision {
    private final BuildAction action;
    private final int targetId;

    private ActionDecision(BuildAction action, int targetId) {
        this.action = action;
        this.targetId = targetId;
    }

    public static ActionDecision none() {
        return new ActionDecision(BuildAction.NONE, -1);
    }

    public static ActionDecision road(int pathId) {
        return new ActionDecision(BuildAction.ROAD, pathId);
    }

    public static ActionDecision settlement(int nodeId) {
        return new ActionDecision(BuildAction.SETTLEMENT, nodeId);
    }

    public static ActionDecision city(int nodeId) {
        return new ActionDecision(BuildAction.CITY, nodeId);
    }

    public BuildAction getAction() {
        return action;
    }

    public int getTargetId() {
        return targetId;
    }
}
