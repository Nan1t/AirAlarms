# Air alarm Ukraine

Simple Android application to check for air alarms in Ukraine.
App uses same backend that [official application](https://play.google.com/store/apps/details?id=com.ukrainealarm).

Application has additional options. You can:

* Change signal volume
* Switch vibration on/off
* Set own ringtones for alerts
* Support for multiple languages

# One important thing

To right work of application, it has service to check alarm status periodically.
Make sure that your android system allow to run app foreground services when main app is down.
If this service works, you can see non-removable notification in tray.