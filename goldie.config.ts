// Goldie 0.5.0 schema, verified against its installed src/config.ts.
// Both targets deliberately use the same screenshot-only app identifier.
export default {
  appRoot: ".",
  appPath: "screenshots/build/ios/CrewScreenshots.app",
  bundleId: "com.kaanf.crew.screenshots",
  android: {
    appPath: "screenshots/android/build/outputs/apk/debug/android-debug.apk",
    applicationId: "com.kaanf.crew.screenshots",
  },
  devices: ["iphone-6.9"],
  locales: ["en-US"],
  appearance: "dark",
  frame: { variant: "17-pro-silver" },
  theme: {
    background: "#0E0B08",
    headlineColor: "#C8FF3D",
    subheadColor: "#F6EFE3",
    fontFamily: '"Bricolage Grotesque", sans-serif',
    fontFiles: [{
      family: "Bricolage Grotesque",
      files: {
        400: "core/designsystem/src/commonMain/composeResources/font/BricolageGrotesque-Regular.ttf",
        700: "core/designsystem/src/commonMain/composeResources/font/BricolageGrotesque-Bold.ttf",
      },
    }],
    copyHeightRatio: 0.23,
    deviceWidthRatio: 0.84,
    layout: "classic",
  },
  store: {
    name: "Crew", subtitle: { "en-US": "Meet. Play. Connect." },
    developer: "Crew", category: "Social Networking",
    rating: 0, ratingCount: "Demo listing", ageRating: "18+", price: "Free",
    description: { "en-US": "Meet people through face-to-face games, social challenges and shared nights out." },
  },
  scenes: [
    { kind: "screenshot", id: "dashboard", flow: "store-01-dashboard", headline: { "en-US": "Play the Room" } },
    { kind: "screenshot", id: "event-detail", flow: "store-02-event-detail", headline: { "en-US": "Your Next Night Starts Here" } },
    { kind: "screenshot", id: "rps", flow: "store-03-rps", headline: { "en-US": "Rock. Paper. Social." } },
    { kind: "screenshot", id: "quest", flow: "store-04-quest", headline: { "en-US": "Dare to Connect" } },
    { kind: "screenshot", id: "leaderboard", flow: "store-05-leaderboard", headline: { "en-US": "Every Match Counts" } },
    { kind: "screenshot", id: "tonights-roll", flow: "store-06-tonights-roll", headline: { "en-US": "Tonight’s Your Story" } },
  ],
};
