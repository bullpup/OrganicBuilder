package uk.org.squirm3.engine;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import uk.org.squirm3.listener.IAtomListener;
import uk.org.squirm3.listener.ILevelListener;
import uk.org.squirm3.listener.IReactionListener;
import uk.org.squirm3.listener.ISpeedListener;
import uk.org.squirm3.listener.IStateListener;


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


public final class EngineDispatcher {

	private final EventListenerList listeners = new EventListenerList();
	
    public void addAtomListener(IAtomListener listener) {
        listeners.add(IAtomListener.class, listener);
    }
    
    public void removeAtomListener(IAtomListener listener) {
        listeners.remove(IAtomListener.class, listener);
    }
    
    public EventListener[] getAtomListeners() {
        return listeners.getListeners(IAtomListener.class);
    }

    public void addLevelListener(ILevelListener listener) {
        listeners.add(ILevelListener.class, listener);
    }
    
    public void removeLevelListener(ILevelListener listener) {
        listeners.remove(ILevelListener.class, listener);
    }

    protected EventListener[] getLevelListeners() {
        return listeners.getListeners(ILevelListener.class);
    }

    public void addSpeedListener(ISpeedListener listener) {
        listeners.add(ISpeedListener.class, listener);
    }
    
    public void removeSpeedListener(ISpeedListener listener) {
        listeners.remove(ISpeedListener.class, listener);
    }
    
    protected EventListener[] getSpeedListeners() {
        return listeners.getListeners(ISpeedListener.class);
    }
    
    public void addReactionListener(IReactionListener listener) {
        listeners.add(IReactionListener.class, listener);
    }
    
    public void removeReactionListener(IReactionListener listener) {
        listeners.remove(IReactionListener.class, listener);
    }
    
    protected EventListener[] getReactionListeners() {
        return listeners.getListeners(IReactionListener.class);
    }

    public void addStateListener(IStateListener listener) {
        listeners.add(IStateListener.class, listener);
    }
    
    public void removeStateListener(IStateListener listener) {
        listeners.remove(IStateListener.class, listener);
    }
    
    protected EventListener[] getStateListeners() {
        return listeners.getListeners(IStateListener.class);
    }
    
    protected void atomsHaveChanged() {
		EventListener[] atomListeners = getAtomListeners();
		for(int i = 0; i < atomListeners.length ; i++) {
			((IAtomListener)atomListeners[i]).atomsHaveChanged();
		}
	}

    protected void draggingPointHasChanged() {
		EventListener[] atomListeners = getAtomListeners();
		for(int i = 0; i < atomListeners.length ; i++) {
			((IAtomListener)atomListeners[i]).draggingPointHasChanged();
		}
	}

    protected void levelHasChanged() {
		EventListener[] levelListeners = getLevelListeners();
		for(int i = 0; i < levelListeners.length ; i++) {
			((ILevelListener)levelListeners[i]).levelHasChanged();
		}
	}
	
    protected void configurationHasChanged() {
		EventListener[] levelListeners = getLevelListeners();
		for(int i = 0; i < levelListeners.length ; i++) {
			((ILevelListener)levelListeners[i]).configurationHasChanged();
		}
	}

    protected void reactionsHaveChanged() {
		EventListener[] reactionListeners = getReactionListeners();
		for(int i = 0; i < reactionListeners.length ; i++) {
			((IReactionListener)reactionListeners[i]).reactionsHaveChanged();
		}
	}

    protected void simulationSpeedHasChanged() {
		EventListener[] propertyListeners = getSpeedListeners();
		for(int i = 0; i < propertyListeners.length ; i++) {
			((ISpeedListener)propertyListeners[i]).simulationSpeedHasChanged();
		}
	}

    protected void simulationStateHasChanged() {
		EventListener[] stateListeners = getStateListeners();
		for(int i = 0; i < stateListeners.length ; i++) {
			((IStateListener)stateListeners[i]).simulationStateHasChanged();
		}
	}
}
