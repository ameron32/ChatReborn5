package com.ameron32.chatreborn5;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Static Loader Class, load various elements all at once through the run()
 * method.
 * 
 * @author klemeilleur
 * 
 */

public class Loader {
  
  // STATIC LOADER CLASS
  
  private static Context context;
  
  public static void run(Context context) {
    Loader.context = context;
    typefaces();
  }

  /**
   * Match this enum with the corresponding font.ttf filename in assets/fonts/
   */
  public enum Fonts {
    daedra, dragonscript, anirm, elvencommonspeak, elvishringnfi, ringm, dwarvesc, dethekstone, temphisdirty, neutonregular, genbasr, genbkbasb
  }
  
  public static final HashMap<Fonts, Typeface> fonts = new HashMap<Fonts, Typeface>();
  
  private static void typefaces() {
    for (Fonts f : Fonts.values()) {
      putFont(f.name());
    }
  }
  
  private static void putFont(String font) {
    fonts.put(Fonts.valueOf(font), Typeface.createFromAsset(context.getAssets(), "fonts/" + font + ".ttf"));
  }
}
