package world.chiyogami.chiyogamilib;

public enum ServerType {
    CRAFT_BUKKIT("CraftBukkit"),
    PAPER("Paper"),
    CHIYOGAMI("Chiyogami");
    
    private final String name;
    
    ServerType(String name){
        this.name = name;
    }
    
    
    public static ServerType getTypeByName(String name){
        for(ServerType serverType : ServerType.values()){
            if(serverType.name.equals(name)){
                return serverType;
            }
        }
        
        return CRAFT_BUKKIT;
    }
}
