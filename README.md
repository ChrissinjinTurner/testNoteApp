# testNoteApp
This is a test repository that I use to test features before implementing them into a production application.
Right now I'm using it to test a custom note taking experience. 

## Feature List
### New global notes
Status: Done

### New notes tied to highlighted position
Status: Done

### Backing up with the cloud 
Status: Done

### Render Outline function
Status: Done

### Header Item
Status: Needs more work

### Data Storage
Status: Done

### Integrating with API
Status: Done

### Non breaking space
Status: Done

### Boxes over hidden blanks
Status: mostly done, needs more testing and stuff

### Remove Notes
Status: In Progress

### Misc features
  - (Done) Click on a span to remove it instead of having a clear button
  - (Done) Change color of text within highlight like i did with underline
  - (Done) Highlights and underlines dissappear after clicking on them and not saying yes. 
  - (Done) I need to clean up the toggle keyboard function, it basically toggles it. Which i know is the name, but i need it to more check if its open and leave it open or if its closed open it type of thing. 
  - (Done) Need to find a better way to handle links than what i was using, it was causing graphical glitches.
    - I found an improved version of the LinkMovementMethod, it works a lot better now I just have to see if we actually want to include this class in the project (The additions are minimal but still possibly affecting).  
  - (Done) Remember if keyboard is opened or closed and keep it like that (so that they aren't opening the keyboard everytime they open myNotes)
    - I see one problem with this, it closes the keyboard whenever you close the dialog, so i'm not sure how i want to handle this
    - (Done) open keyboard if theres nothing in edittext else leave it closed
  - (Done) What i need to do is move the preference file check into the oncreate with the network call (in an if statement). Then only call fill outline if a preference file is found
  - (Done) Refactor to no longer use local variables, only always use the outlineItem
  - (Done) Add in an error message in case the outline doesn't load something like "The Outline failed to load, please close and try again"
  - (Done) match font sizes from outline headings and content into wrapup block
  - (Done) Match font of title to font of headings
  - (Kinda Done) Test with changing system font size
    - Seems to work, nothing crazy goes on when you change the font size
  - (Done) look into dashed underline for notes
    - Dashed lines do not seem like a feasible option
  - (Done) copy notes item from image sent by thomas

### Fix when the app is killed in the background while outline is open
Status: Done
