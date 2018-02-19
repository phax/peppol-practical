# peppol-practical
The source code of the [peppol.helger.com](http://peppol.helger.com) website.
This project requires Java 1.8 for building and running. *Note*: JDK 1.8.0 does not work because of classloader issues! Successfully tested with JDK 1.8.0_20 (and above).

This project is licensed under the Apache 2.0 License.

## Running
To run this application locally either clone it or download it.
Load the project into Eclipse (best to use "Import | Maven | Import existing Maven project...").
Run `com.helger.peppol.jetty.RunInJettyPP` from within Eclipse - that launches a Jetty.
Open `http://localhost:8080/` in your browser.

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
