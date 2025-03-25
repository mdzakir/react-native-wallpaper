import {NativeModules} from 'react-native';

const {RNWallpaper} = NativeModules;

export default {
  /**
   * Set a wallpaper with specific positioning options
   *
   * @param {string} uri - Local file path or remote URL to image
   * @param {object} options - Configuration options
   * @param {boolean} options.isSystem - Set as system wallpaper (default: true)
   * @param {boolean} options.isLock - Set as lock screen wallpaper (default: false)
   * @param {boolean} options.centerHorizontally - Center wallpaper horizontally (default: true)
   * @returns {Promise<string>} - Resolves when wallpaper is set
   */

  setWallpaper(uri, options = {}) {
    // Check if the module exists
    console.log('Available modules:', Object.keys(NativeModules));
    console.log('Our module:', RNWallpaper);
    return RNWallpaper.setWallpaper(uri, options);
  },
};
