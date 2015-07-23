## In-App Consent APK Overview

This repository is comprised of two workspaces: __InAppConsent__ and __Use_InAppConsentSDK__.

####InAppConsent
This workspace is where the In-App Consent APK is built. It includes these two projects:

InAppConsentSDK project: This is the SDK project. It can be built and used directly as a module, exported as an AAR and exported as a JAR.

Use_SDK_module project: This project uses the InAppConsentSDK project as a dependent module. It is used to rapidly develop and test the InAppConsentSDK without having to export it as a library.

####Use_InAppConsentSDK
This workspace is used to develop test apps that include InAppConsentSDK as an AAR or as a JAR. It includes these three projects:

InAppConsentSDK: This project is a duplicate of the InAppConsentSDK project from the __InAppConsent__ workspace with these changes:

1. The java source code has been removed.

1. The JAR that is exported from InAppConsent.InAppConsentSDK is included in the libs folder.

1. The test project has been removed.

This project is included in the Use_SDK_jar project as a module and provides In-App Consent functionality to the resulting IceCube app.

Use_SDK_aar: This project includes the InAppConsentSDK project as an AAR and provides In-App Consent functionality to the resulting WaterDrop app. The WaterDrop app excersises the AAR version of the SDK.

Use_SDK_jar: This project includes the InAppConsentSDK project as a JAR-based module and provides In-App Consent functionality to the resulting IceCube app. The IceCube app excersises the AAR version of the SDK.


####Build the AAR and JAR SDKs

1.	Update all version names and version numbers for both the SDKs and the test apps.
    *	...\InAppConsent\InAppConsentSDK\build.gradle
    *	...\InAppConsent\Use_SDK_module\build.gradle
    *	...\Use_InAppConsentSDK\InAppConsentSDK\build.gradle
    *	...\Use_InAppConsentSDK\Use_SDK_aar\build.gradle
    *	...\Use_InAppConsentSDK\Use_SDK_jar\build.gradle

1.	Clean the InAppConsent workspace. This causes the AAR library files to be rebuilt.

1.	Build the JAR file manually via Gradle
    *	Expand the Gradle panel in Android Studio
    *	Double-click InAppConsent.InAppConsentSDK.jarrelease

1.	Copy "outputs" folder:
    * From: ...\InAppConsent_android\InAppConsent\InAppConsentSDK\build\
    *	To: ...\InAppConsent_android\release\

1.  Rename the release AAR file (remove "-release" from filename):
    * From: ...\InAppConsent_android\release\outputs\aar\InAppConsentSDK-release.aar
    * To: InAppConsentSDK.aar

1.  Delete     
    * ...\InAppConsent_android\release\outputs\aar\InAppConsentSDK-debug.aar

1.	Create a new "InAppConsentSDK" folder in the "outputs" folder (this will be the folder for the JAR SDK):
    *	...\InAppConsent_android\release\outputs\

1.	Copy the "libs" folder into the JAR SDK folder:
    * From: ...\InAppConsent_android\InAppConsent\InAppConsentSDK\build\
    *	To: ...\InAppConsent_android\release\outputs\InAppConsentSDK\

1.	Copy the "src" folder into the JAR SDK folder:
    * From: ...\InAppConsent_android\InAppConsent\InAppConsentSDK\
    *	To: ...\InAppConsent_android\release\outputs\InAppConsentSDK\

1.  Delete these output folders:
    * ...\InAppConsent_android\release\outputs\InAppConsentSDK\src\androidTest\
    * ...\InAppConsent_android\release\outputs\InAppConsentSDK\src\main\java\

1.	Open the InAppConsentSDK.jar zip container (or unzip and rezip after edit) and remove all .class files in this folder (I use 7-Zip for this):
    * ...\InAppConsent_android\Use_InAppConsentSDK\InAppConsentSDK\libs\InAppConsentSDK.jar\com\ghostery\privacy\inappconsentsdk\

```
        R$anim.class
        R$attr.class
        R$bool.class
        R$color.class
        R$dimen.class
        R$drawable.class
        R$id.class
        R$integer.class
        R$layout.class
        R$string.class
        R$style.class
        R$styleable.class
        R.class
```


####Update the AAR Test App with the latest SDK

1.	Copy the AAR file from the release "outputs" folder to the AAR project's libs folder:
    * From: ...\release\outputs\aar\InAppConsentSDK.aar
    *	To: this folder: ...\InAppConsent_android\Use_InAppConsentSDK\Use_SDK_aar\libs\


####Customize the SDK for the AAR test app (optional)

1.	Unzip InAppConsentSDK.aar to a new folder

1.	Edit applicable strings in the "ghostery_strings.xml" section of:
    * ...\InAppConsent_android\Use_InAppConsentSDK\Use_SDK_aar\libs\InAppConsentSDK\res\values\values.xml

    *	For example:
	```
	      ghostery_manage_preferences_description
	        	From "Our company with help from..."
	        	To "(YourCompanyName) with help from..."
	      ghostery_dialog_header_text
	        	From "We Care About Your Privacy"
	        	To "(YourCompanyName) Cares About Your Privacy"
	```

    *	Customization examples for AAR WaterDrop strings:
	```xml
    <string name="ghostery_dialog_explicit_message">The WaterDrop app uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to explicitly tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. To give us your consent, click on the \"Accept\" button.</string>
    <string name="ghostery_dialog_header_text">WaterDrop Cares About Your Privacy</string>
    <string name="ghostery_dialog_implicit_message">The WaterDrop app uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. Further use of this app will be considered consent.</string>
    <string name="ghostery_manage_preferences_description">WaterDrop with help from our partners, collects data about your use of this app. We respect your privacy and if you would like to limit the data we collect please use the control panel below. To find out more about how we use data please visit our privacy policy.</string>
	```

1.	Zip the contents of the unzipped AAR back into a ZIP file

1.	Copy that ZIP file back to libs folder it came from.

1.	Delete the old AAR file and rename the new ZIP file to InAppConsentSDK.aar



####Update the JAR Test App with the latest SDK

1.	Delete the "libs" and "src" folders from: 
    * ...\InAppConsent_android\Use_InAppConsentSDK\InAppConsentSDK\

1.	Copy the "libs" and "src" folders:
    * From: ...\InAppConsent_android\release\outputs\InAppConsentSDK\
    * To: ...\InAppConsent_android\Use_InAppConsentSDK\InAppConsentSDK\


####Customize the SDK for the JAR test app (optional)

1.	Edit applicable strings in:
    * ...\InAppConsent_android\Use_InAppConsentSDK\InAppConsentSDK\src\main\res\values\ghostery_strings.xml

    *	For example:
	```
	      ghostery_manage_preferences_description
	        	From "Our company with help from..."
	        	To "(YourCompanyName) with help from..."
	      ghostery_dialog_header_text
	        	From "We Care About Your Privacy"
	        	To "(YourCompanyName) Cares About Your Privacy"
	```

    *	Customization examples for JAR IceCube strings:
	```xml
    <string name="ghostery_dialog_explicit_message">The IceCube app uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to explicitly tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. To give us your consent, click on the \"Accept\" button.</string>
    <string name="ghostery_dialog_header_text">IceCube Cares About Your Privacy</string>
    <string name="ghostery_dialog_implicit_message">The IceCube app uses technologies so that we, and our partners, can remember you and understand how you use our app. To see a complete list of these technologies and to tell us whether they can be used on your device, click on the \"Manage Preferences\" button below. Further use of this app will be considered consent.</string>
    <string name="ghostery_manage_preferences_description">IceCube with help from our partners, collects data about your use of this app. We respect your privacy and if you would like to limit the data we collect please use the control panel below. To find out more about how we use data please visit our privacy policy.</string>
	```


####Build the test APKs

1.  Rename the release "outputs" folder:
    * From: ...\release\outputs\
    * To: ...\release\outputs_(version name)-(version number)\

1.	Clean and rebuild the Use_InAppConsentSDK workspace (containing both the AAR and the JAR test projects).

1.  Test both the AAR and the JAR test modules in debug-mode.

1. Build a new AAR APK:
   1. In Android Studio, Click the menu Build > Generate Signed APK...
   1. Select the "Use_SDK_aar" module in the dropdown and click Next.
   1. Enter the applicable keystore path, keystore password, key alias (evidon), and key password. (Get these from LastPass.) Then click Next.
   1. Set the APK Destination folder as: ...\InAppConsent_android\release\Use_SDK_aar\
   1. Select the Build Type as "release".
   1. Click Finish.
   
1.  Rename the new AAR APK file:
    * From: ...\InAppConsent_android\release\Use_SDK_aar\Use_SDK_aar-release.apk
    * To: Use_SDK_aar_(version name)-(version number).apk

1.  Delete the new manifest report file:
    * From: ...\InAppConsent_android\release\Use_SDK_aar\manifest-merger-release-report.txt

1. Build a new JAR APK:
   1. In Android Studio, Click the menu Build > Generate Signed APK...
   1. Select the "Use_SDK_jar" module in the dropdown and click Next.
   1. Enter the applicable keystore path, keystore password, key alias (evidon), and key password. (Get these from LastPass.) Then click Next.
   1. Set the APK Destination folder as: ...\InAppConsent_android\release\Use_SDK_aar\
   1. Select the Build Type as "release".
   1. Click Finish.
   
1.  Rename the new AAR APK file:
    * From: ...\InAppConsent_android\release\Use_SDK_jar\Use_SDK_jar-release.apk
    * To: Use_SDK_jar_(version name)-(version number).apk

1.  Delete the new manifest report file:
    * From: ...\InAppConsent_android\release\Use_SDK_jar\manifest-merger-release-report.txt
1.  Test both the AAR and the JAR test APKs by copying to an ANdroid device.

1.  Check the signed APKs and the SDK folders into GitHub.
