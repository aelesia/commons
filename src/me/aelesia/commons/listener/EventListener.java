package me.aelesia.commons.listener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseListener;

public class EventListener {
	
	NativeKeyListener keyEvent = null;
	NativeMouseListener mouseEvent = null;
	
	public EventListener() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
        	throw new RuntimeException("There was a problem registering the native hook: " + ex.getMessage(), ex);
        }
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
	}
	
	public void attachKeyListener(NativeKeyListener keyEvent) {
		detatchKeyListener();
		this.keyEvent = keyEvent;
		GlobalScreen.addNativeKeyListener(keyEvent);
	}
	
	public void detatchKeyListener() {
		GlobalScreen.removeNativeKeyListener(keyEvent);
		this.keyEvent = null;
	}
	
	public void attachCtrlC(AtomicBoolean exit) {
		this.keyEvent = new NativeKeyListener() {
			boolean ctrl = false;
			boolean c = false;
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getKeyCode() == 29) {
					ctrl = true;
				} else if (e.getKeyCode() == 46) {
					c = true;
				}
				
				if (ctrl && c) {
					detatchAll();
					exit.set(true);
				}
			}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {
				if (e.getKeyCode() == 29) {
					ctrl = false;
				} else if (e.getKeyCode() == 46) {
					c = false;
				}
			}
			
			@Override
			public void nativeKeyTyped(NativeKeyEvent arg0) {}
		};
		GlobalScreen.addNativeKeyListener(keyEvent);
	}
	
	public void attachMouseListener(NativeMouseListener mouseEvent) {
		detatchMouseListener();
		this.mouseEvent = mouseEvent;
		GlobalScreen.addNativeMouseListener(mouseEvent);
	}
	
	public void detatchMouseListener() {
		GlobalScreen.removeNativeMouseListener(mouseEvent);
		this.mouseEvent = null;
	}
	
	public void detatchAll() {
		detatchMouseListener();
		detatchKeyListener();
	}
}