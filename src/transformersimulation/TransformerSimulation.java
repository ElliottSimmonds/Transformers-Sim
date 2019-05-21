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
			selection();
        }
        autobots.forEach(autobot -> autobot.act(cybertron));
    }

    private void selection() {
        List<Autobot> genePool = generateGenePool();
        autobots.clear();
        cybertron.clear();
        
        resources = new Resource(new Location(15,2));
        cybertron.setAgent(resources, resources.getLocation());
        
        for (int i=0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
            Autobot parent = genePool.get(TransformerConfig.random.nextInt(genePool.size()));
            
            Autobot baby = reproduce(parent);
            autobots.add(baby);
            cybertron.setAgent(baby, baby.getLocation());
        }
        System.out.println("Babies created");
    }

    private Autobot reproduce(Autobot parent) {
        Autobot baby = new Autobot(new Location(2,15));
        
        List<Location> parentPath = parent.getPath();
        List<Location> babyPath = new ArrayList<Location>();

        babyPath.add(parentPath.get(0));
        for (int i=1; i < parentPath.size(); i++) {
            int babyX = parentPath.get(i).getX() - parentPath.get(i-1).getX();
            int babyY = parentPath.get(i).getY() - parentPath.get(i-1).getY();
			
			babyPath.add(new Location(babyX, babyY));
            
        }
        baby.setPath(babyPath);
        return baby;
    }
    
    private List<Autobot> generateGenePool() {
        List<Autobot> genePool = new ArrayList<Autobot>();
        autobots.forEach(autobot -> {
            int numAutobots = (int)((autobot.getFitness()*10) * TransformerConfig.MAX_TRANSFORMERS);
            for (int i=0; i < numAutobots; i++) {
                genePool.add(autobot.cloneTransformer());
            }
        } );
        return genePool;
    }
    
}
