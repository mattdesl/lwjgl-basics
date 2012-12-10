Render Tangent-Space Normal Map Pass

--

Idea from Roy Schulz:
http://download.blender.org/documentation/bc2004/Roy_Schulz/normal.html

Before rendering:
- Adjust the position of the lights to your coordinate system.
- Use the "WhiteNormalMaterial" on your objects to ensure proper illumination
- In Scene > Color Management, use "None" as the Display Device and "Raw" for Color Space. 
- Ensure your objects are centered correctly, for e.g. xyz=(0, 0, 0)
- Use ortho projection (Numpad 5) and front (Numpad 1), then Ctrl + Alt + Numpad 0 to set the camera to that view.

Project by Matt DesLauriers (davedes). More details to come in an article:
https://github.com/mattdesl/lwjgl-basics/wiki