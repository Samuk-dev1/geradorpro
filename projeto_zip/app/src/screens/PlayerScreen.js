import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Video, ResizeMode } from 'expo-av';
import colors from '../styles/colors';
import { getStreamUrl } from '../services/api';

export default function PlayerScreen({ route, navigation }) {
  const { channel, serverData } = route.params;
  const [streamUrl, setStreamUrl] = useState(null);

  useEffect(() => {
    if (serverData && channel) {
      const url = getStreamUrl(serverData.url, serverData.user, serverData.pass, channel.stream_id);
      setStreamUrl(url);
    }
  }, [channel, serverData]);

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()} style={styles.backButton}>
          <Text style={styles.backText}>← Voltar</Text>
        </TouchableOpacity>
        <Text style={styles.title} numberOfLines={1}>{channel.name}</Text>
      </View>

      <View style={styles.videoContainer}>
        {streamUrl ? (
          <Video
            style={styles.video}
            source={{ uri: streamUrl }}
            useNativeControls
            resizeMode={ResizeMode.CONTAIN}
            isLooping
            shouldPlay
            onError={(error) => console.log('Erro no vídeo:', error)}
          />
        ) : (
          <Text style={styles.loadingText}>Carregando stream...</Text>
        )}
      </View>
      
      <View style={styles.infoContainer}>
        <Text style={styles.infoText}>Canal: {channel.name}</Text>
        <Text style={styles.infoText}>Categoria: {channel.category_name || 'N/A'}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#000' },
  header: { flexDirection: 'row', alignItems: 'center', padding: 15, backgroundColor: colors.surface },
  backButton: { marginRight: 15 },
  backText: { color: colors.primary, fontSize: 16, fontWeight: 'bold' },
  title: { color: colors.text, fontSize: 18, fontWeight: 'bold', flex: 1 },
  videoContainer: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: '#000' },
  video: { width: '100%', height: '100%' },
  loadingText: { color: colors.text, fontSize: 16 },
  infoContainer: { padding: 20, backgroundColor: colors.surface },
  infoText: { color: colors.textSecondary, fontSize: 14, marginBottom: 5 }
});