# StreamFlix – Movie / Video Streaming Demo App (JavaFX)

Group Assignment (40%) · Faculty of ICT · Diploma in Multimedia with Software Engineering / BSc Software Engineering and Multimedia

A Netflix-style demo that lists movies by genre, shows a hover effect on every thumbnail, and plays the official trailer **directly from YouTube inside the app** with custom controls, a custom loading animation, custom buttons and sound effects. No video or image assets are stored in the project.

---

## 1. How to run

**Requirements:** JDK 17 or newer, Maven 3.8+, an internet connection (thumbnails and trailers come from YouTube).

```bash
cd streamflix
mvn clean javafx:run
```

**In an IDE (IntelliJ / Eclipse / NetBeans):** open the folder as a Maven project, let it download dependencies, and run `com.streamflix.Main` (it is a plain launcher class that starts `StreamFlixApp`, so the VS Code / IDE Run button works).

---

## 2. Brief requirement → implementation

| # | Requirement in the brief | Where / how it is done |
|---|---|---|
| 1 | ≥ 3 categories, each with ≥ 8 videos | `data/MovieCatalog.java` – **4 categories × 10 movies** |
| 2 | Categories clearly labelled | `CategoryRow.buildHeader()` – red accent bar, bold title, "10 titles" count; nav bar links jump to each row |
| 3 | Thumbnail image + movie title | `ThumbnailCard` – 16:9 YouTube thumbnail with the title underneath |
| 4 | Visually appealing hover effect | `ThumbnailCard.installHover()` – zoom 110 %, red glow, white border, dark gradient + play badge + year fade in, title brightens, tick sound |
| 5 | Only 5 thumbnails visible per category, scroll to the rest | `HomeView` binds card width so exactly 5 fit; `CategoryRow` scrolls by 5 cards with animated arrow buttons, and supports trackpad/horizontal-wheel swipe |
| 6 | Click title/thumbnail → trailer from YouTube, played inside the app | `PlayerView` – JavaFX `WebView` loads the YouTube embed URL (`Movie.getEmbedUrl()`); nothing is downloaded or stored |
| 7 | Play and pause | `PlayerView.togglePlayback()` drives the page's HTML5 `<video>` through `WebEngine.executeScript` (plus ±10 s and fullscreen) |
| 8 | Custom loading animation | `LoadingAnimation` – three counter-rotating arcs, pulsing logo, animated "Loading trailer…" caption; fades out when playback starts |
| 9 | No `Button` class – custom buttons | `CustomButton` – built from `StackPane` + `Label` + vector icon; own hover/press animation, click sound, keyboard (Enter/Space) support. `grep -r "javafx.scene.control.Button" src` returns nothing |
| 10 | Sound effects | `SoundManager` – hover tick, click, "ta-dum" open/intro, back; synthesised in code (no audio files); mute toggle in the nav bar |

### Rubric mapping (120 marks)

| Rubric criterion | Evidence in the project |
|---|---|
| 1. Application (20) | All 10 brief requirements above |
| 2. UI design (10) | Dark Netflix-style theme in one stylesheet (`streamflix.css`), hero banner, nav bar, intro splash |
| 3. Categories (10) | 4 labelled genres × 10 movies, easy to extend in `MovieCatalog` |
| 4. Thumbnail display (10) | 5-per-row layout that resizes with the window, smooth animated scrolling |
| 5. Hover & animations (10) | Thumbnail hover, button hover/press, hero entrance, loading animation, splash, fading player controls |
| 6. Video playback (15) | YouTube embed in `WebView`, play/pause, seek, fullscreen, timeout + "Watch on YouTube" fallback |
| 7. Custom controls (5) | `CustomButton` + `IconFactory` (all icons are drawn shapes) |
| 8. Sound (5) | `SoundManager` + mute toggle |
| 9. Code quality (10) | Packages `model / data / audio / ui`, one class per responsibility, Javadoc comments |
| 10. Git (10) | See section 4 – follow the workflow so every member has real commits |
| 11. Understanding (15) | See section 5 – viva preparation |

---

## 3. Project structure

```
src/main/java/com/streamflix/
├── Main.java / Launcher.java      plain entry points (start StreamFlixApp)
├── StreamFlixApp.java             the JavaFX Application (window + stylesheet)
├── model/    Movie, Category      plain data classes (immutable)
├── data/     MovieCatalog         the movie list – edit here to add movies
├── audio/    SoundManager         synthesised sound effects + mute
└── ui/
    ├── AppRoot                    stacks Home / Player / Splash and handles navigation
    ├── HomeView                   nav bar + hero + category rows, responsive card width
    ├── NavBar, HeroBanner         top navigation and featured banner
    ├── CategoryRow                header + 5-card scrolling strip + arrows
    ├── ThumbnailCard              thumbnail + title + hover effect
    ├── PlayerView                 WebView player + custom controls + error handling
    ├── LoadingAnimation           custom loading animation
    ├── CustomButton, IconFactory  custom buttons and vector icons
src/main/resources/css/streamflix.css   the whole theme
```

**Design ideas worth knowing**

* **OOP:** each visual element is its own class that extends a layout node (`ThumbnailCard extends VBox`, `CustomButton extends StackPane`, …) and hides its internals. `Movie` and `Category` are immutable.
* **Responsive 5-card layout:** `cardWidth = (pageWidth − padding − 4 gaps) / 5` is a JavaFX *binding* shared by every card, so resizing the window rescales all rows automatically.
* **YouTube control:** YouTube's own controls are switched off (`controls=0`). The app finds the page's `<video>` element and calls `play()` / `pause()` / `currentTime` from Java. A 400 ms `Timeline` polls the element to learn whether it is loading, playing or paused.
* **Why a Chrome user-agent:** YouTube often refuses to serve its player to the default JavaFX WebView user-agent.

---

## 4. Git workflow (rubric item 10 – 10 marks)

The marks are for **frequent, meaningful commits from every member, branching, merges and clear messages**, so do this as a team from day one:

1. One member creates the GitHub repo, pushes this project to `main`, and adds the others as collaborators.
2. Protect `main`: nobody commits directly; all work goes through feature branches and Pull Requests reviewed by another member.
3. Suggested branches and owners (rename to your real names):

| Member | Branch | Task |
|---|---|---|
| Member 1 | `feature/catalog-and-model` | `Movie`, `Category`, `MovieCatalog` (check every YouTube id works) |
| Member 2 | `feature/home-layout` | `HomeView`, `NavBar`, `HeroBanner`, CSS theme |
| Member 3 | `feature/category-rows` | `CategoryRow`, `ThumbnailCard`, hover effect, scrolling |
| Member 4 | `feature/video-player` | `PlayerView`, play/pause, error handling |
| Member 5 | `feature/custom-ui-and-sound` | `CustomButton`, `IconFactory`, `LoadingAnimation`, `SoundManager` |

4. Commit small and often, with messages in the form `type: what changed`, e.g.
   `feat: add hover glow and play badge to ThumbnailCard`, `fix: stop audio when leaving player`, `style: tune nav bar spacing`, `docs: add run instructions`.
5. Merge with Pull Requests (keeps merge history visible). Do not squash everything into one commit.

> The project was handed to you as a finished code base. For the git mark and for the viva, **each member must really commit their own part** – for example by re-implementing, extending or refining the class they own (new hover effect, extra sound, more movies, bug fixes) in their own branch.

---

## 5. Viva preparation (rubric item 11 – 15 marks)

Every member should be able to answer these:

* **How do you play YouTube inside JavaFX?** `WebView` loads `https://www.youtube.com/embed/<id>`; the video is streamed, never saved.
* **How do play/pause work if YouTube's controls are hidden?** `engine.executeScript(...)` runs JavaScript that finds `<video>` and calls `play()` or `pause()`.
* **How do you know when the video has started?** A `Timeline` polls `video.readyState`/`video.paused` every 400 ms; when it reports "playing" the loading animation fades out.
* **How do you show exactly five thumbnails?** Card width is bound to `(width − padding − gaps)/5`; each row is a `ScrollPane` with the scroll bars hidden and arrow buttons that animate `hvalue`.
* **How is the hover effect built?** `setOnMouseEntered` runs a `ScaleTransition`, a `FadeTransition` on the overlay, adds a `DropShadow`, plays a sound; `setOnMouseExited` reverses it. `setViewOrder(-1)` draws the card above neighbours without changing layout.
* **How did you avoid the `Button` class?** `CustomButton` is a `StackPane` with a `Label`/icon and mouse + key handlers.
* **Where do sounds come from?** `SoundManager` synthesises sine tones with an envelope into PCM and plays them with `javax.sound.sampled.Clip` – no audio files.
* **How do you add a movie?** One line in `MovieCatalog`: `m("Title", year, "YouTubeId")`.

---

## 6. Troubleshooting

* **A thumbnail shows only a coloured box with the title** – the YouTube id for that movie is wrong or you are offline. Open `https://i.ytimg.com/vi/<ID>/mqdefault.jpg` in a browser to test an id, and correct it in `MovieCatalog`.
* **"Trailer unavailable" screen** – YouTube blocked embedding for that video, or no internet. Use "Watch on YouTube" or replace the id with another official trailer that allows embedding.
* **Trailer does not play / stays on a YouTube error page** – JavaFX's WebView uses an older browser engine than Chrome. Update `javafx.version` in `pom.xml` to the newest JavaFX release and make sure you are on a recent JDK.
* **No sound** – check the speaker toggle in the nav bar and your system volume.
