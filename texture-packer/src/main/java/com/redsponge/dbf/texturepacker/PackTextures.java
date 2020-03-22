package com.redsponge.dbf.texturepacker;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackTextures {

    public static void main(String[] args) {
        System.out.println("HAI");
        proc("player");
        proc("target");
        proc("octopus");
    }

    private static void proc(String s) {
        System.out.println("Processing " + s);
        TexturePacker.processIfModified("raw/" + s, "../assets/textures/" + s, s);
    }

}
