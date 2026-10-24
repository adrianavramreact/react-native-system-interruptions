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
  type AudioRoute,
  type CallEvent,
  type OtherAudioEvent,
} from 'react-native-system-interruptions';

export default function App() {
  const [callEvents, setCallEvents] = useState<CallEvent[]>([]);
  const [otherAudioEvents, setOtherAudioEvents] = useState<OtherAudioEvent[]>(
    []
  );
  const [routeEvents, setRouteEvents] = useState<AudioRoute[][]>([]);

  useEffect(() => {
    const callSub = addListener('call', (event) => {
      setCallEvents((prev) => [event, ...prev]);
    });
    const otherAudioSub = addListener('otherAudio', (event) => {
      setOtherAudioEvents((prev) => [event, ...prev]);
    });
    const routeSub = addListener('audioRoute', ({ routes }) => {
      setRouteEvents((prev) => [routes, ...prev]);
    });

    return () => {
      callSub.remove();
      otherAudioSub.remove();
      routeSub.remove();
    };
  }, []);

  const checkAvailable = async () => {
    const call = await SystemInterruptions.isAvailableAsync('call');
    const otherAudio = await SystemInterruptions.isAvailableAsync('otherAudio');
    ToastAndroid.show(
      `call: ${call}, otherAudio: ${otherAudio}`,
      ToastAndroid.SHORT
    );
  };

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

  const checkAudioRoute = () => {
    SystemInterruptions.getAudioRouteAsync()
      .then((routes) =>
        ToastAndroid.show(
          `audioRoute: ${routes.join(', ')}`,
          ToastAndroid.SHORT
        )
      )
      .catch((e) =>
        ToastAndroid.show(`${e.code}: ${e.message}`, ToastAndroid.LONG)
      );
  };

  return (
    <View style={styles.container}>
      <Button title="isAvailable" onPress={checkAvailable} />
      <Button title="isInCall" onPress={checkInCall} />
      <Button title="isOtherAudioPlaying" onPress={checkOtherAudio} />
      <Button title="getAudioRoute" onPress={checkAudioRoute} />
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

      <Text style={styles.title}>audioRoute</Text>
      <ScrollView style={styles.list}>
        {routeEvents.map((routes, i) => (
          <Text key={i}>{routes.join(', ')}</Text>
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
