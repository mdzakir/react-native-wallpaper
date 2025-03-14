# Wallpaper Manager for React Native

A React Native module that gives you fine-grained control over wallpaper positioning on Android.

## Installation

```bash
npm install --save @mdzakir/react-native-wallpaper
# or
yarn add @mdzakir/react-native-wallpaper
```

### Linking (React Native < 0.60)

```bash
react-native link @mdzakir/react-native-wallpaper
```

For React Native 0.60 and above, the module will be auto-linked.

### Manual Linking (if needed)

#### Android

1. Open `android/app/src/main/java/[...]/MainApplication.java`
2. Add `import com.your.package.name.WallpaperPackage;` to the imports section
3. Add `packages.add(new WallpaperPackage());` to the list in the `getPackages()` method

## Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.SET_WALLPAPER" />
<uses-permission android:name="android.permission.INTERNET" /> <!-- If loading from URLs -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> <!-- If loading from file system -->
```

## Usage

```javascript
import WallpaperManager from '@mdzakir/react-native-wallpaper';

// Set wallpaper with centering
WallpaperManager.setWallpaper(
  '/path/to/image.jpg', // Local file path or URL
  {
    isSystem: true, // Set as system wallpaper (default: true)
    isLock: false, // Set as lock screen wallpaper (default: false)
    centerHorizontally: true, // Center wallpaper horizontally (default: true)
  },
)
  .then(() => console.log('Wallpaper set successfully'))
  .catch(error => console.error('Failed to set wallpaper:', error));
```

## Example Usage

```javascript
import React from 'react';
import {Button, View} from 'react-native';
import WallpaperManager from '@mdzakir/react-native-wallpaper';

function WallpaperScreen() {
  const setWallpaper = async () => {
    try {
      // Local image example
      await WallpaperManager.setWallpaper(
        '/storage/emulated/0/Download/wallpaper.jpg',
        {
          isSystem: true,
          isLock: false,
          centerHorizontally: true,
        },
      );

      // OR remote image example
      // await WallpaperManager.setWallpaper(
      //   'https://example.com/wallpaper.jpg',
      //   { centerHorizontally: true }
      // );

      console.log('Wallpaper set successfully');
    } catch (error) {
      console.error('Failed to set wallpaper:', error);
    }
  };

  return (
    <View>
      <Button title="Set Wallpaper" onPress={setWallpaper} />
    </View>
  );
}

export default WallpaperScreen;
```

## API

### setWallpaper(uri, options)

Sets an image as the device wallpaper with fine-grained positioning control.

#### Parameters:

```json
- uri (String): Local file path or remote URL to the image
- options (Object):
  - isSystem (Boolean): Set as system wallpaper (default: true)
  - isLock (Boolean): Set as lock screen wallpaper (default: false)
  - centerHorizontally (Boolean): Center wallpaper horizontally (default: true)
```

#### Returns:

- Promise that resolves when the wallpaper is set

## Troubleshooting

- For Pixel devices and other newer Android phones, make sure `centerHorizontally` is set to `true`
- If wallpaper still appears shifted, try using an image with dimensions that match your device's screen resolution
- For older Android versions (<7.0), only basic wallpaper setting is supported without positioning options
