// Android-only entry point for hosts without an installed iOS simulator runtime.
import config from "./goldie.config.ts";
export default {
  ...config,
  devices: ["pixel-10-pro"],
  android: {
    ...config.android,
    // A neutral frame preserves the emulator's rectangular status-bar content.
    // Goldie's bundled Pixel bezel masks part of the clock on this AVD/runtime.
    frame: {
      image: "screenshots/marketing/android-frame.svg",
      width: 1410,
      height: 2986,
      screen: { x: 65, y: 65, width: 1280, height: 2856 },
      screenRadius: 32,
    },
  },
  scenes: config.scenes.map(scene => ({ ...scene, flow: `android-${scene.flow}` })),
};
