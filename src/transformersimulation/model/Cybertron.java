/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformersimulation.model;

import java.util.ArrayList;
import java.util.List;
import prins.simulator.Config;
import prins.simulator.model.Agent;
import prins.simulator.model.Environment;
import prins.simulator.model.Location;

/**
 *
 * @author 4simme28
 */
public class Cybertron extends Environment{
    
    private Agent[][] cybertron;
    
    public Cybertron() {
        cybertron = new Agent[Config.world_width][Config.world_height];
    }
    
    @Override
    public void clear() {
        cybertron = new Agent[Config.world_width][Config.world_height];
    }

    @Override
    public Agent getAgent(Location location) {
        return cybertron[location.getY()][location.getX()];
    }

    @Override
    public int getHeight() {
        return Config.world_height;
    }

    @Override
    public int getWidth() {
        return Config.world_width;
    }

    @Override
    public void setAgent(Agent agent, Location location) {
        cybertron[location.getY()][location.getX()] = agent;
    }
    
    public List<Location> findAdjacentLocations(Location location) {
        List<Location> adjacentLocations = new ArrayList<>();
        
        for (int yOffset=-1; yOffset<=1; yOffset++) {
            for (int xOffset=-1; xOffset<=1; xOffset++) {
                int x = location.getX() + xOffset;
                int y = location.getY() + yOffset;
                if (x >= 0 && y >= 0 && x < Config.world_width && y < Config.world_height) {
                    adjacentLocations.add(new Location(x,y));
                }
            }
        }
        
        return adjacentLocations;
    }
    
}
