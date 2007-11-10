package uk.org.squirm3;

import java.util.Properties;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import uk.org.squirm3.engine.IApplicationEngine;
import uk.org.squirm3.engine.LocalEngine;
import uk.org.squirm3.ui.GUI;
import uk.org.squirm3.ui.Resource;

/**  
Copyright 2007 Tim J. Hutton, Ralph Hartley, Bertrand Dechoux

This file is part of Organic Builder.

Organic Builder is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

Organic Builder is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Organic Builder; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

public final class Application  {

	private static Application currentApplication;
	
	public static void runAsApplet(JApplet applet) {
		if(currentApplication!=null) return;
		new Application(applet);
	}
	
	public static void runAsStandaloneApplication() {
		if(currentApplication!=null) return;
		new Application();
	}
	
	public static void main(String argv[]) {
		runAsStandaloneApplication();
	}
	
	// path to finds the files needed for the translations
	private final String levelsTranslationFilePath;
	private final String interfaceTranslationFilePath;
	// instances to store the translations (key-value)
	private final Properties levelsProps = new Properties();
	private final Properties interfaceProps = new Properties();
	
	// path of the file needed for the configuration
	private static final String configurationFilePath = "/configuration.properties";
	// instance to store the configuration (key-value)
	private final Properties configurationProps = new Properties();
	// string returned if the value wasn't found for translation
	public static final String translationError = "ERROR : STRING NOT FOUND!";

	private Application(final JApplet applet) {
		currentApplication = this;
		// load configuration
		try{ configurationProps.load(Resource.class.getResourceAsStream(configurationFilePath));
		} catch(Exception e) {}
		// choose a language
		String chosenLanguage = Application.getConfiguration(new String[] {"languages", "choice"});
		if(chosenLanguage==null) {
			final String[] languagesArray = Application.getConfiguration(new String[] {"languages", "available"}).split(" ");
			if(languagesArray.length==1) chosenLanguage = languagesArray[0];
			else chosenLanguage = GUI.selectLanguage(languagesArray);
		}
		// load translation
		levelsTranslationFilePath = Application.getConfiguration(new String[] {"translation", "levels"});
		interfaceTranslationFilePath = Application.getConfiguration(new String[] {"translation", "interface"});
			// use files in the specified language
			// for more information, see http://www.loc.gov/standards/iso639-2/englangn.html
		try {
			levelsProps.load(Resource.class.getResourceAsStream(levelsTranslationFilePath+"_"+chosenLanguage+".properties"));
		    interfaceProps.load(Resource.class.getResourceAsStream(interfaceTranslationFilePath+"_"+chosenLanguage+".properties"));
		} catch(Exception e) {// use default files
			try {
				levelsProps.load(Resource.class.getResourceAsStream(levelsTranslationFilePath+".properties"));
				interfaceProps.load(Resource.class.getResourceAsStream(interfaceTranslationFilePath+".properties"));
			} catch(Exception ex) {;}
		}
		final IApplicationEngine iApplicationEngine = new LocalEngine();
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	GUI.createGUI(iApplicationEngine, applet);
	        }
	    });
	}
	
	private Application() { this(null); }
	
	static public String localize(String[] code) {
		if(currentApplication==null) return null;
		final String bundle = code[0];
		String key = code[1];
		for( int i = 2 ; i<code.length ; i++) {
			key += "."+code[i];
		}
		if(bundle.equals("levels")) {
			return currentApplication.levelsProps.getProperty(key,translationError);
		} else if(bundle.equals("interface")) {
			return currentApplication.interfaceProps.getProperty(key,translationError);
		} else return bundle+"/"+key;
	}

	static public String getConfiguration(String[] code) {
		if(currentApplication==null) return null;
		String key = code[0];
		for( int i = 1 ; i<code.length ; i++) {
			key += "."+code[i];
		}
		return currentApplication.configurationProps.getProperty(key, null);
	}
}