package com.starfishst.test;

import com.starfishst.api.addons.Addon;

public class TestAddon extends Addon {

    @Override
    public void onEnable(){
        System.out.println("Hi!");
    }

}
