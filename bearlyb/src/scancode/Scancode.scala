package bearlyb.scancode

opaque type Scancode = Int

object Scancode:
  val A: Scancode = 4
  val B: Scancode = 5
  val C: Scancode = 6
  val D: Scancode = 7
  val E: Scancode = 8
  val F: Scancode = 9
  val G: Scancode = 10
  val H: Scancode = 11
  val I: Scancode = 12
  val J: Scancode = 13
  val K: Scancode = 14
  val L: Scancode = 15
  val M: Scancode = 16
  val N: Scancode = 17
  val O: Scancode = 18
  val P: Scancode = 19
  val Q: Scancode = 20
  val R: Scancode = 21
  val S: Scancode = 22
  val T: Scancode = 23
  val U: Scancode = 24
  val V: Scancode = 25
  val W: Scancode = 26
  val X: Scancode = 27
  val Y: Scancode = 28
  val Z: Scancode = 29

  val K1: Scancode = 30
  val K2: Scancode = 31
  val K3: Scancode = 32
  val K4: Scancode = 33
  val K5: Scancode = 34
  val K6: Scancode = 35
  val K7: Scancode = 36
  val K8: Scancode = 37
  val K9: Scancode = 38
  val K0: Scancode = 39

  val Return: Scancode = 40
  val Escape: Scancode = 41
  val Backspace: Scancode = 42
  val Tab: Scancode = 43
  val Space: Scancode = 44

  val Minus: Scancode = 45
  val Equals: Scancode = 46
  val LeftBracket: Scancode = 47

  /** Located at the lower left of the return key on ISO keyboards and at the
    * right end of the QWERTY row on ANSI keyboards. Produces REVERSE SOLIDUS
    * (backslash) and VERTICAL LINE in a US layout, REVERSE SOLIDUS and VERTICAL
    * LINE in a UK Mac layout, NUMBER SIGN and TILDE in a UK Windows layout,
    * DOLLAR SIGN and POUND SIGN in a Swiss German layout, NUMBER SIGN and
    * APOSTROPHE in a German layout, GRAVE ACCENT and POUND SIGN in a French Mac
    * layout, and ASTERISK and MICRO SIGN in a French Windows layout.
    */
  val RightBracket: Scancode = 48
  val Backslash: Scancode = 49

  /** ISO USB keyboards actually use this code instead of 49 for the same key,
    * but all OSes I've seen treat the two codes identically. So, as an
    * implementor, unless your keyboard generates both of those codes and your
    * OS treats them differently, you should generate case BACKSLASH extends
    * Scancode(instead)of this code. As a user, you should not rely on this code
    * because SDL will never generate it with most (all?) keyboards.
    */
  val NonUSHash: Scancode = 50
  val Semicolon: Scancode = 51
  val Apostrophe: Scancode = 52

  /** Located in the top left corner (on both ANSI and ISO keyboards). Produces
    * GRAVE ACCENT and TILDE in a US Windows layout and in US and UK Mac layouts
    * on ANSI keyboards, GRAVE ACCENT and NOT SIGN in a UK Windows layout,
    * SECTION SIGN and PLUS-MINUS SIGN in US and UK Mac layouts on ISO
    * keyboards, SECTION SIGN and DEGREE SIGN in a Swiss German layout (Mac:
    * only on ISO keyboards), CIRCUMFLEX ACCENT and DEGREE SIGN in a German
    * layout (Mac: only on ISO keyboards), SUPERSCRIPT TWO and TILDE in a French
    * Windows layout, COMMERCIAL AT and NUMBER SIGN in a French Mac layout on
    * ISO keyboards, and LESS-THAN SIGN and GREATER-THAN SIGN in a Swiss German,
    * German, or French Mac layout on ANSI keyboards.
    */
  val Grave: Scancode = 53
  val Comma: Scancode = 54
  val Period: Scancode = 55
  val Slash: Scancode = 56

  val Capslock: Scancode = 57

  val F1: Scancode = 58
  val F2: Scancode = 59
  val F3: Scancode = 60
  val F4: Scancode = 61
  val F5: Scancode = 62
  val F6: Scancode = 63
  val F7: Scancode = 64
  val F8: Scancode = 65
  val F9: Scancode = 66
  val F10: Scancode = 67
  val F11: Scancode = 68
  val F12: Scancode = 69

  val Printscreen: Scancode = 70
  val Scrolllock: Scancode = 71
  val Pause: Scancode = 72

  /** insert on PC, help on some Mac keyboards (but does send code 73, not 117)
    */
  val Insert: Scancode = 73
  val Home: Scancode = 74
  val PageUp: Scancode = 75
  val Delete: Scancode = 76
  val End: Scancode = 77
  val PageDown: Scancode = 78
  val Right: Scancode = 79
  val Left: Scancode = 80
  val Down: Scancode = 81
  val Up: Scancode = 82

  val NumlockClear: Scancode = 83

  /** < num lock on PC, clear on Mac keyboards */
  val KpDivide: Scancode = 84
  val KpMultiply: Scancode = 85
  val KpMinus: Scancode = 86
  val KpPlus: Scancode = 87
  val KpEnter: Scancode = 88
  val Kp1: Scancode = 89
  val Kp2: Scancode = 90
  val Kp3: Scancode = 91
  val Kp4: Scancode = 92
  val Kp5: Scancode = 93
  val Kp6: Scancode = 94
  val Kp7: Scancode = 95
  val Kp8: Scancode = 96
  val Kp9: Scancode = 97
  val Kp0: Scancode = 98
  val KpPeriod: Scancode = 99

  /** This is the additional key that ISO keyboards have over ANSI ones, located
    * between left shift and Y. Produces GRAVE ACCENT and TILDE in a US or UK
    * Mac layout, REVERSE SOLIDUS (backslash) and VERTICAL LINE in a US or UK
    * Windows layout, and LESS-THAN SIGN and GREATER-THAN SIGN in a Swiss
    * German, German, or French layout.
    */
  val NonUSBackslash: Scancode = 100
  val Application: Scancode = 101

  /** < windows contextual menu, compose */
  val Power: Scancode = 102

  /** < The USB document says this is a status flag, not a physical key - but
    * some Mac keyboards do have a power key.
    */
  val KpEquals: Scancode = 103
  val F13: Scancode = 104
  val F14: Scancode = 105
  val F15: Scancode = 106
  val F16: Scancode = 107
  val F17: Scancode = 108
  val F18: Scancode = 109
  val F19: Scancode = 110
  val F20: Scancode = 111
  val F21: Scancode = 112
  val F22: Scancode = 113
  val F23: Scancode = 114
  val F24: Scancode = 115
  val Execute: Scancode = 116
  val Help: Scancode = 117

  /** < AL Integrated Help Center */
  val Menu: Scancode = 118

  /** < Menu (show menu) */
  val Select: Scancode = 119
  val Stop: Scancode = 120

  /** < AC Stop */
  val Again: Scancode = 121

  /** < AC Redo/Repeat */
  val Undo: Scancode = 122

  /** < AC Undo */
  val Cut: Scancode = 123

  /** < AC Cut */
  val Copy: Scancode = 124

  /** < AC Copy */
  val Paste: Scancode = 125

  /** < AC Paste */
  val Find: Scancode = 126

  /** < AC Find */
  val Mute: Scancode = 127
  val VolumeUp: Scancode = 128
  val VolumeDown: Scancode = 129
  val KpComma: Scancode = 133
  val KpEqualsas400: Scancode = 134

  val International1: Scancode = 135

  /** < used on Asian keyboards, see footnotes in USB doc */
  val International2: Scancode = 136
  val International3: Scancode = 137

  /** < Yen */
  val International4: Scancode = 138
  val International5: Scancode = 139
  val International6: Scancode = 140
  val International7: Scancode = 141
  val International8: Scancode = 142
  val International9: Scancode = 143
  val Lang1: Scancode = 144

  /** < Hangul/English toggle */
  val Lang2: Scancode = 145

  /** < Hanja conversion */
  val Lang3: Scancode = 146

  /** < Katakana */
  val Lang4: Scancode = 147

  /** < Hiragana */
  val Lang5: Scancode = 148

  /** < Zenkaku/Hankaku */
  val Lang6: Scancode = 149

  /** < reserved */
  val Lang7: Scancode = 150

  /** < reserved */
  val Lang8: Scancode = 151

  /** < reserved */
  val Lang9: Scancode = 152

  /** < reserved */

  val AltErase: Scancode = 153

  /** < Erase-Eaze */
  val SysReq: Scancode = 154
  val Cancel: Scancode = 155

  /** < AC Cancel */
  val Clear: Scancode = 156
  val Prior: Scancode = 157
  val Return2: Scancode = 158
  val Separator: Scancode = 159
  val Out: Scancode = 160
  val Oper: Scancode = 161
  val ClearAgain: Scancode = 162
  val Crsel: Scancode = 163
  val Exsel: Scancode = 164

  val Kp00: Scancode = 176
  val Kp000: Scancode = 177
  val ThousandsSeparator: Scancode = 178
  val DecimalSeparator: Scancode = 179
  val CurrencyUnit: Scancode = 180
  val CurrencySubunit: Scancode = 181
  val KpLeftParen: Scancode = 182
  val KpRightParen: Scancode = 183
  val KpLeftBrace: Scancode = 184
  val KpRightBrace: Scancode = 185
  val KpTab: Scancode = 186
  val KpBackspace: Scancode = 187
  val KpA: Scancode = 188
  val KpB: Scancode = 189
  val KpC: Scancode = 190
  val KpD: Scancode = 191
  val KpE: Scancode = 192
  val KpF: Scancode = 193
  val KpXor: Scancode = 194
  val KpPower: Scancode = 195
  val KpPercent: Scancode = 196
  val KpLess: Scancode = 197
  val KpGreater: Scancode = 198
  val KpAmpersand: Scancode = 199
  val KpDblampersand: Scancode = 200
  val KpVerticalbar: Scancode = 201
  val KpDblverticalbar: Scancode = 202
  val KpColon: Scancode = 203
  val KpHash: Scancode = 204
  val KpSpace: Scancode = 205
  val KpAt: Scancode = 206
  val KpExclam: Scancode = 207
  val KpMemstore: Scancode = 208
  val KpMemrecall: Scancode = 209
  val KpMemclear: Scancode = 210
  val KpMemadd: Scancode = 211
  val KpMemsubtract: Scancode = 212
  val KpMemmultiply: Scancode = 213
  val KpMemdivide: Scancode = 214
  val KpPlusminus: Scancode = 215
  val KpClear: Scancode = 216
  val KpClearentry: Scancode = 217
  val KpBinary: Scancode = 218
  val KpOctal: Scancode = 219
  val KpDecimal: Scancode = 220
  val KpHexadecimal: Scancode = 221

  val LCtrl: Scancode = 224
  val LShift: Scancode = 225

  /** < alt, option */
  val LAlt: Scancode = 226

  /** < windows, command (apple), meta */
  val LGUI: Scancode = 227
  val RCtrl: Scancode = 228
  val RShift: Scancode = 229

  /** < alt gr, option */
  val Ralt: Scancode = 230

  /** < windows, command (apple), meta */
  val RGUI: Scancode = 231

  /** < I'm not sure if this is really not covered by any of the above, but
    * since there's a special SDL_KMOD_MODE for it I'm adding it here
    */
  val Mode: Scancode = 257

  /** < Sleep */
  val Sleep: Scancode = 258

  /** < Wake */
  val Wake: Scancode = 259

  /** < Channel Increment */
  val ChannelIncrement: Scancode = 260

  /** < Channel Decrement */
  val ChannelDecrement: Scancode = 261

  /** < Play */
  val MediaPlay: Scancode = 262

  /** < Pause */
  val MediaPause: Scancode = 263

  /** < Record */
  val MediaRecord: Scancode = 264

  /** < Fast Forward */
  val MediaFastForward: Scancode = 265

  /** < Rewind */
  val MediaRewind: Scancode = 266

  /** < Next Track */
  val MediaNextTrack: Scancode = 267

  /** < Previous Track */
  val MediaPreviousTrack: Scancode = 268

  /** < Stop */
  val MediaStop: Scancode = 269

  /** < Eject */
  val MediaEject: Scancode = 270

  /** < Play / Pause */
  val MediaPlayPause: Scancode = 271
  // Media Select
  val MediaSelect: Scancode = 272

  /** < AC New */
  val AcNew: Scancode = 273

  /** < AC Open */
  val AcOpen: Scancode = 274

  /** < AC Close */
  val AcClose: Scancode = 275

  /** < AC Exit */
  val AcExit: Scancode = 276

  /** < AC Save */
  val AcSave: Scancode = 277

  /** < AC Print */
  val AcPrint: Scancode = 278

  /** < AC Properties */
  val AcProperties: Scancode = 279

  /** < AC Search */
  val AcSearch: Scancode = 280

  /** < AC Home */
  val AcHome: Scancode = 281

  /** < AC Back */
  val AcBack: Scancode = 282

  /** < AC Forward */
  val AcForward: Scancode = 283

  /** < AC Stop */
  val AcStop: Scancode = 284

  /** < AC Refresh */
  val AcRefresh: Scancode = 285

  /** < AC Bookmarks */
  val AcBookmarks: Scancode = 286

  /* \name Mobile keys
   *
   * These are values that are often used on mobile phones. */

  /** < Usually situated below the display on phones and used as a
    * multi-function feature key for selecting a software defined function shown
    * on the bottom left of the display.
    */
  val SoftLeft: Scancode = 287

  /** < Usually situated below the display on phones and used as a
    * multi-function feature key for selecting a software defined function shown
    * on the bottom right of the display.
    */
  val SoftRight: Scancode = 288
  val Call: Scancode = 289

  /** < Used for accepting phone calls. */
  val Endcall: Scancode = 290

  /** < Used for rejecting phone calls. */

  opaque type Dyn <: Scancode = Int

  object Dyn:
    def apply(code: Int): Dyn = code
    def unapply(dyn: Dyn): Some[Int] = Some(dyn)

  extension (code: Scancode) private[bearlyb] def internal: Scancode = code

  private[bearlyb] def fromInternal(internal: Int): Scancode = internal

end Scancode
