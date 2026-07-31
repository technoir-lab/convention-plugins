// swift-tools-version: 6.2
import PackageDescription

let package = Package(
  name: "Greeting",
  platforms: [.macOS(.v26)],
  products: [.library(name: "Greeting", targets: ["Greeting"])],
  targets: [
    .target(
      name: "Greeting",
      swiftSettings: [
        .define("GREETING_DEBUG", .when(configuration: .debug)),
        .define("GREETING_RELEASE", .when(configuration: .release)),
      ]
    )
  ]
)
