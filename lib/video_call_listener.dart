/// Class holding the callback functions for conference events
class VideoCallListener {
  final Function({Map<dynamic, dynamic>? message})? onConferenceWillJoin;
  final Function({Map<dynamic, dynamic>? message})? onConferenceJoined;
  final Function({Map<dynamic, dynamic>? message})? onConferenceTerminated;
  final Function({Map<dynamic, dynamic>? message})? onPictureInPictureWillEnter;
  final Function({Map<dynamic, dynamic>? message})?
      onPictureInPictureTerminated;
  final Function({Map<dynamic, dynamic>? message})? onWhiteboardClicked;
  final Function(dynamic error)? onError;

  VideoCallListener(
      {this.onConferenceWillJoin,
      this.onConferenceJoined,
      this.onConferenceTerminated,
      this.onPictureInPictureWillEnter,
      this.onPictureInPictureTerminated,
      this.onWhiteboardClicked,
      this.onError});
}
