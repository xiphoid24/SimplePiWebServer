import java.util.HashMap;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class RPi {

	private GpioController gpio;
	private GpioPinDigitalOutput ledPin;
	HashMap<Character, boolean[]> morse = new HashMap<Character, boolean[]>();
	HashMap<Character, Integer[]> test = new HashMap<Character, Integer[]>();
	private boolean busy = false;
	private boolean ledOn;

	{
		test.put('s', new Integer[] {0,0,0});
		test.put('o', new Integer[] {1,1,1});
	}

	public RPi() {
		this.gpio = GpioFactory.getInstance();
	}

	public void setLedPin(Pin pin) {
		this.ledPin = this.gpio.provisionDigitalOutputPin(pin, PinState.LOW);
	}
	
	public boolean getLedOn() {
		return this.ledOn;
	}

	public void on() {
		this.ledOn = true;
		this.ledPin.setState(PinState.HIGH);
	}

	public void off() {
		this.ledOn = false;
		this.ledPin.setState(PinState.LOW);
	}

	public void toggle() {
		this.ledPin.toggle();
	}

	public void morseCode(char[] code) throws InterruptedException, PiInUseException {
		if (!this.busy) {
			this.busy = true;
			for (char c : code) {
				boolean[] ba = this.morse.get(c);
				if (ba != null) {
					for (boolean b : ba) {
						if (b) {                     // long
							this.on();
							Thread.sleep(500);
							this.off();
							Thread.sleep(250);
						} else {                     // short
							this.on();
							Thread.sleep(150);
							this.off();
							Thread.sleep(250);
						}
					}
				}
			}
			this.busy = false;
		} else {
			throw new PiInUseException();
		}
	}


	public void longPulse() {
		this.ledPin.pulse(500);
	}

	public void shortPulse() {
		this.ledPin.pulse(100);
	}

	{
		morse.put('a', new boolean[] {false,true});
		morse.put('b', new boolean[] {true,false,false,false});
		morse.put('c', new boolean[] {false,true,false,true});
		morse.put('d', new boolean[] {true,false,false});
		morse.put('e', new boolean[] {false});
		morse.put('f', new boolean[] {false,false,true,false});
		morse.put('g', new boolean[] {true,true,false});
		morse.put('h', new boolean[] {false,false,false,false});
		morse.put('i', new boolean[] {false,false});
		morse.put('j', new boolean[] {false,true,true,true});
		morse.put('k', new boolean[] {true,false,true});
		morse.put('l', new boolean[] {false,true,false,false});
		morse.put('m', new boolean[] {true,true});
		morse.put('n', new boolean[] {true,false});
		morse.put('o', new boolean[] {true,true,true});
		morse.put('p', new boolean[] {false,true,true,false});
		morse.put('q', new boolean[] {true,true,false,true});
		morse.put('r', new boolean[] {false,true,false});
		morse.put('s', new boolean[] {false,false,false});
		morse.put('t', new boolean[] {true});
		morse.put('u', new boolean[] {false,false,true});
		morse.put('v', new boolean[] {false,false,false,true});
		morse.put('w', new boolean[] {false,true,true});
		morse.put('x', new boolean[] {true,false,false,true});
		morse.put('y', new boolean[] {true,false,false,false});
		morse.put('z', new boolean[] {true,true,false,false});
	}
}