import { useEffect, useState } from 'react';
import {
  Button,
  Text,
  View,
  StyleSheet,
  ScrollView,
  ToastAndroid,
} from 'react-native';
import SystemInterruptions, {
  addListener,
  type CallEvent,
  type OtherAudioEvent,
} from 'react-native-system-interruptions';

export default function App() {
  const [callEvents, setCallEvents] = useState<CallEvent[]>([]);
  const [otherAudioEvents, setOtherAudioEvents] = useState<OtherAudioEvent[]>(
    []
  );

  useEffect(() => {
    const callSub = addListener('call', (event) => {
      setCallEvents((prev) => [event, ...prev]);
    });
    const otherAudioSub = addListener('otherAudio', (event) => {
      setOtherAudioEvents((prev) => [event, ...prev]);
    });

    SystemInterruptions.startObservingAsync().catch((e) =>
      ToastAndroid.show(`${e.code}: ${e.message}`, ToastAndroid.LONG)
    );

    return () => {
      callSub.remove();
      otherAudioSub.remove();
      SystemInterruptions.stopObservingAsync().catch((e) => {
        console.warn('stopObservingAsync failed', e);
      });
    };
  }, []);

  const checkInCall = () => {
    SystemInterruptions.isInCallAsync()
      .then((inCall) =>
        ToastAndroid.show(`isInCall: ${inCall}`, ToastAndroid.SHORT)
      )
      .catch((e) =>
        ToastAndroid.show(`${e.code}: ${e.message}`, ToastAndroid.LONG)
      );
  };

  const checkOtherAudio = () => {
    SystemInterruptions.isOtherAudioPlayingAsync()
      .then((playing) =>
        ToastAndroid.show(`isOtherAudioPlaying: ${playing}`, ToastAndroid.SHORT)
      )
      .catch((e) =>
        ToastAndroid.show(`${e.code}: ${e.message}`, ToastAndroid.LONG)
      );
  };

  return (
    <View style={styles.container}>
      <Button title="isInCall" onPress={checkInCall} />
      <Button title="isOtherAudioPlaying" onPress={checkOtherAudio} />
      <Text style={styles.title}>call</Text>
      <ScrollView style={styles.list}>
        {callEvents.map((e, i) => (
          <Text key={i}>{e}</Text>
        ))}
      </ScrollView>

      <Text style={styles.title}>otherAudio</Text>
      <ScrollView style={styles.list}>
        {otherAudioEvents.map((e, i) => (
          <Text key={i}>{e}</Text>
        ))}
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    paddingTop: 64,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginTop: 8,
  },
  list: {
    flex: 1,
  },
});
