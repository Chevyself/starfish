package com.starfishst.test;

import com.starfishst.bot.addons.Addon;
import com.starfishst.bot.addons.AddonInformation;
import org.jetbrains.annotations.NotNull;

public class TestAddon extends Addon {

    @Override
    public void onEnable(){
        System.out.println("Hi!");
    }

}
