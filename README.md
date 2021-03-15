
# react-native-bixolon-printer

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-bixolon-printer` and add `RNBixolonPrinter.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNBixolonPrinter.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNBixolonPrinterPackage;` to the imports at the top of the file
  - Add `new RNBixolonPrinterPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-bixolon-printer'
  	project(':react-native-bixolon-printer').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-bixolon-printer/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-bixolon-printer')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNBixolonPrinter.sln` in `node_modules/react-native-bixolon-printer/windows/RNBixolonPrinter.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Bixolon.Printer.RNBixolonPrinter;` to the usings at the top of the file
  - Add `new RNBixolonPrinterPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNBixolonPrinter from 'react-native-bixolon-printer';

// TODO: What to do with the module?
RNBixolonPrinter;
```
  
