package com.zenoshrdlu.turborix;

/******************************************************************************
*                                                                             *
* Interface for Turborix User Interface                                       *
* Dave Mitchell dave@zenoshrdlu.com                                           *
*                                                                             *
******************************************************************************/
                                  


public interface TurborixUI {

   public void notify(int channel, int value);
   public void notifySettings(SettingData sdata);
   public void setMessage(String m);
   public void addLogMsg(String t);
}

