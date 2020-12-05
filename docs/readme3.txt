Team Raspberry Iteration 3 Readme

Our style guide is adapted from Cornell University's Department of Computer Science
Java Code Style Guidelines
by David Gries
http://www.cs.cornell.edu/courses/JavaAndDS/JavaStyle.html

Breath Activity:
There is some small delay between pressing the button and the phone registering the press,
this may make it seem like the button is held for longer than three seconds before allowing
the user to transition to the Exhale stage (by releasing) but the button indeed does allow
the user to transition to exhaling after three seconds of holding the button according to
when the system registers the button as being pressed. Hard evidence of this is in the running
log, which will print how long the button was held in the inhale stage upon release. Any hardware
or system delay is not the responsibility of team raspberry.

The state machine says after three seconds of pressing the button for inhaling the button state
changes to "Out," and after releasing the button the button becomes "Out." This doesn't make much
sense. The state of the button isn't entirely viewable to the user, so changing the state to "Out"
doesn't necessarily mean the text changes to "Out," but changing the button to "Out" does. Instead
of trying to follow the state machine exactly, for this issue, we've decided to follow the iter-
ation three description of what needs to happen, since it is clearer for this particular issue.
The following clarification outlines what we did regarding this issue.

In the inhale stage, after three seconds of holding the button the text of the button changes
to "Out," but the color of the button stays green, the inhale colour, and when exhaling, after
three seconds the text of the button changes to "In" (if the user has breaths left to do) but
the colour stays blue, the exhale colour. This is by design and comes from following the iteration
three description very closely.

Timeout Timer:
A toast is displayed when the speed of the timer is changed, but this is not the "subtle" message
that a child would have trouble finding outlined in the iteration three description. The real
subtle message is in black text in the top left corner when the timer is running.