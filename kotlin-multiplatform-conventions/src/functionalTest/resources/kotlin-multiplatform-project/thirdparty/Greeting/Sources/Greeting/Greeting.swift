import Foundation

@objc public final class Greeting: NSObject {
  @objc public static func message() -> String {
    #if GREETING_DEBUG
      "Hello, Swift (debug)"
    #elseif GREETING_RELEASE
      "Hello, Swift (release)"
    #else
      "Hello, Swift (unknown)"
    #endif
  }
}
