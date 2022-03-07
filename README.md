# Directional Haptic Feedback Application from ZANE Wearables
**ZANE Wearables is Zian Lin, Amy Koh, and Niklas Chang.**

Developed for IEEE's QP program. Won 'Most Technical' award at IEEE'S WI22 QP Showcase.

# Summary
This application aims to translate directional instructions into haptic feedback to assist people who require handsfree navigation for one reason or another. Instead of having to keep the phone in a holder or wear earphones to get audio instructions, directions can be understood at any time with a simple three-vibration code: one short vibration for going left, two short vibrations for going right, and one long vibration for holding the current direction. The user experience is simple and easy to understand: all the user needs to do is input their destination, select a method of transport (biking, walking, or driving) and a long vibration will signal that the journey has begun. This application finds its best use for people on bikes, electric scooters, or skateboards.

![image](https://user-images.githubusercontent.com/64982992/156991553-c94eee61-7a18-42f4-830b-a3105d5bbd97.png)

*Fig. 1: The user interface.*

# API keys
This app relies on the Google Maps API. In order for this application to work on your device, you will need to supply your own API key and replace 'api_key' in strings.xml before building the app and moving the APK to your device. API keys can be obtained at [Google Cloud Console APIs](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwjaovvPxrP2AhUJmWoFHYAUD6YQFnoECA0QAQ&url=https%3A%2F%2Fconsole.cloud.google.com%2F&usg=AOvVaw1GxwHR1WZnDu0xsR-djCrv), and you will not be charged if you request under 28,500 maploads a month.

    <string name="google_maps_api">YOUR_KEY_HERE</string>
