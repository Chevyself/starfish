package me.googas.test;

import me.googas.addons.java.JavaAddon;

public class TestAddon extends JavaAddon {

    @Override
    public void onEnable() throws Throwable {
        this.getLogger().info("Test addon has been init");
    }
}
