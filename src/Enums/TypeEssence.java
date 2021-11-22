package Enums;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TypeEssence {
    GAZOLE(1),
    E85(3),
    E10(5),
    SP98(6);

    Integer id;
    public Integer getId(){return this.id;}
    TypeEssence(Integer id){
        this.id = id;
    }

    static Map<Integer, TypeEssence> lookup = new HashMap();
    static { Arrays.stream(TypeEssence.values()).forEach(t -> lookup.put(t.getId(), t));}
}
