/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webbrar.mods.biggerhousesmod;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.structures.Structure;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

/**
 *
 * @author Webba
 */
public class BiggerHousesMod implements WurmMod, Configurable{
    private Logger logger = Logger.getLogger(this.getClass().getName());
    public static float carpentryMultiplier = 1.0f;

    @Override
    public void configure(Properties properties) {
        try {
        BiggerHousesMod.carpentryMultiplier = Float.valueOf(properties.getProperty("carpentryMultiplier", Float.toString(BiggerHousesMod.carpentryMultiplier)));
        HookManager.getInstance().registerHook("com.wurmonline.server.behaviours.MethodsStructure", "hasEnoughSkillToExpandStructure", "(Lcom/wurmonline/server/creatures/Creature;IILcom/wurmonline/server/structures/Structure;)Z", new InvocationHandlerFactory(){
                @Override 
                public InvocationHandler createInvocationHandler(){
                    return new InvocationHandler(){
                        @Override
                        public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                            final Creature performer = (Creature)args[0];
                            final int tilex = (int)args[1]; 
                            final int tiley = (int)args[2];  
                            final Structure plannedStructure = (Structure)args[3]; 
                            Skill carpentry = null;
                            try {
                                carpentry = performer.getSkills().getSkill(1005);
                            }
                            catch (NoSuchSkillException nss) {
                                performer.getCommunicator().sendNormalServerMessage("You have no idea of how you would build a house.");
                                return false;
                            }
                            if (carpentry == null) {
                                performer.getCommunicator().sendNormalServerMessage("You have no idea of how you would build a house.");
                                return false;
                            }
                            int limit = 5;
                            if (plannedStructure.getSize() > 1) {
                                limit = plannedStructure.getLimitFor(tilex, tiley, performer.isOnSurface(), true);
                            }
                            else {
                                limit = 5;
                            }
                            if (limit == 0) {
                                performer.getCommunicator().sendNormalServerMessage("The house seems to have no walls. Please replan.");
                                return false;
                            }
                            if (limit > (carpentry.getRealKnowledge() * BiggerHousesMod.carpentryMultiplier)) {
                                performer.getCommunicator().sendNormalServerMessage("You are not skilled enough in Carpentry to build this size of structure.");
                                return false;
                            }
                            return true;
                        }
                    };
                }
            });
        } 
        catch (Exception e) {
            throw new HookException(e);
        }
    }
}
