/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformersimulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import prins.simulator.Simulator;
import prins.simulator.model.Location;
import prins.simulator.view.Gui;
import transformersimulation.model.Autobot;
import transformersimulation.model.Planet;
import transformersimulation.model.Resource;

/**
 *
 * @author 4simme28
 */
public class TransformerSimulation extends Simulator {

    private Planet cybertron;
    private Gui gui;
    private List<Autobot> autobots;
    Resource resources;
    
    public TransformerSimulation() {
        cybertron = new Planet();
        gui = new Gui(cybertron);
        gui.registerAgentColors(Autobot.class, Color.blue);
        gui.registerAgentColors(Resource.class, Color.orange);
        
        populate();
    }
    
    private void populate() {
        resources = new Resource(new Location(15,2));
        cybertron.setAgent(resources, resources.getLocation());
        
        autobots = new ArrayList<>();
        for (int i=0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
            Autobot autobot = new Autobot(new Location(5,15));
            autobot.generatePath(cybertron);
            autobots.add(autobot);
        }
    }
    
    @Override
    protected void prepare() {
        gui.addPropertyChangeListener(this);
    }

    @Override
    protected void render() {
        gui.setStep(step);
        gui.render();
    }

    @Override
    protected void reset() {
        cybertron.clear();
    }

    @Override
    protected void update() {
        if (step % TransformerConfig.MAX_PATH == 0) {
            //work out fitness
            autobots.forEach(autobot -> {
            
            int rocketX = autobot.getLocation().getX();
            int rocketY = autobot.getLocation().getY();
            int targetX = resources.getLocation().getX();
            int targetY = resources.getLocation().getY();
            
            double differenceX = Math.abs(targetX - rocketX);
            double differenceY = Math.abs(targetY - rocketY);
            double difference = Math.sqrt(Math.pow(differenceX,2) + Math.pow(differenceY,2));
            double fitness = 1 / difference;
            
            autobot.setFitness(fitness);
            
            });
        }
        autobots.forEach(autobot -> autobot.act(cybertron));
    }

}
