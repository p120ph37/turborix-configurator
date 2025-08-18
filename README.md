# Turborix Configurator

A modernized Java application for configuring
Turborix/HobbyKing/Flysky 6-channel 2.4GHz radio-control transmitters,
forked from [Dave Mitchell](http://www.zenoshrdlu.com)'s [Turborix
Configurator](http://www.zenoshrdlu.com/turborix/) version
[1.06X](http://www.zenoshrdlu.com/turborix/TXsource.zip).

## Overview

This is a modernized version of the original Turborix Configurator by
Dave Mitchell. The application allows you to configure various
settings on Turborix transmitters (models such as HK-T6A, FS-CT6A,
FS-CT6B, and TBXT6) through a serial connection.

## Building and Running

```bash
# Run the application from the source
./gradlew run

# Build a fat JAR with all dependencies
./gradlew jar

# Run the application from the JAR
java -jar build/libs/turborix-configurator-2.0.0.jar
```

## Serial Drivers

The exact driver you need will vary depending on the serial cable you
are using.  Modern FlySky transmitters come with a CP2102 serial chip
from SI Labs, for which you can find drivers
[here](https://www.silabs.com/software-and-tools/usb-to-uart-bridge-vcp-drivers?tab=downloads).

## Serial Port Setup

By default, this application will try two well-known port names:
- `/dev/tty.usbserial`
- `/dev/tty.SLAB_USBtoUART`

On modern MacOS systems, these are probably not correct, so you will
likely need to add the specific port name to the `turborix.properties`
file (which, if absent, is generated the first time you run the app).

You can see the list of current serial ports on a modern MacOS system
by doing: 
```bash
ls -l /dev/cu.*
```

You will then place the desired port-name into `turborix.properties`
like this:
```
port=/dev/cu.usbserial-0001
```

On Windows, your serial port will have a name like `COM3`, so that is
what you should put into the properties file.

## License

Dave Mitchell's orignial project is released as "Freeware" with a
request for gratuities (AKA,
[Donationware](https://en.wikipedia.org/wiki/Donationware) ).  The
original donation link can be found [on this
page](http://www.zenoshrdlu.com/turborix/)
