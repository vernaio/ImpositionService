package de.perfectpattern.print.imposition.model.type;

public enum Priority {
    Standard(0),
    Express(2),
    Overnight(4);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns the priority by value.
     * @param value The priority vlaue.
     * @return The appropriate priority as enum.
     */
    public static Priority findByValue(int value){
        for(Priority priority : values()){
            if(priority.getValue() == value){
                return priority;
            }
        }
        return null;
    }
}
