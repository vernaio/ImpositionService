package de.perfectpattern.print.imposition.model.type;

public enum FoldCatalog {
    F2_1 ("F2-1"),
    F4_1 ("F4-1");

    private final String name;

    FoldCatalog(String name) {
        this.name = name;
    }

    /**
     * Returns the official name of the fold type.
     * @return The name of the fold type.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the fold type by name.
     * @param name The fold type name.
     * @return The appropriate fold type as enum.
     */
    public static FoldCatalog findByName(String name){
        for(FoldCatalog foldCatalog : values()){
            if(foldCatalog.getName().equalsIgnoreCase(name)){
                return foldCatalog;
            }
        }
        return null;
    }
}
