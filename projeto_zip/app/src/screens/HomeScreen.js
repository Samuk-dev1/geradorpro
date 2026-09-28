import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Image, ActivityIndicator, ScrollView } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import colors from '../styles/colors';
import { getLiveStreams } from '../services/api';

export default function HomeScreen({ navigation }) {
  const [channels, setChannels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [serverData, setServerData] = useState(null);
  const [activeCategory, setActiveCategory] = useState('TV');

  const categories = ['TV', 'JOGOS', 'DESTAQUES', 'FILMES', 'SÉRIES', 'KIDS', 'ANIME', 'EXPLORAR'];

  useEffect(() => {
    loadChannels();
  }, []);

  const loadChannels = async () => {
    try {
      const data = await AsyncStorage.getItem('@iptv_data');
      if (data) {
        const parsed = JSON.parse(data);
        setServerData(parsed);
        const streams = await getLiveStreams(parsed.url, parsed.user, parsed.pass);
        // Filtra apenas canais que não são VOD (opcional, depende do servidor)
        setChannels(streams.slice(0, 100)); // Limita a 100 para não travar
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const renderChannelItem = ({ item }) => (
    <TouchableOpacity 
      style={styles.channelItem} 
      onPress={() => navigation.navigate('Player', { channel: item, serverData })}
    >
      <Image source={{ uri: item.stream_icon }} style={styles.channelLogo} resizeMode="contain" />
      <View style={styles.channelInfo}>
        <Text style={styles.channelNumber}>{item.num}</Text>
        <Text style={styles.channelName} numberOfLines={1}>{item.name}</Text>
      </View>
    </TouchableOpacity>
  );

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={colors.primary} />
        <Text style={styles.loadingText}>Carregando canais...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* Topo - Menu Horizontal */}
      <View style={styles.topMenu}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false}>
          {categories.map((cat) => (
            <TouchableOpacity key={cat} onPress={() => setActiveCategory(cat)}>
              <Text style={[styles.menuItem, activeCategory === cat && styles.menuItemActive]}>{cat}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>

      <View style={styles.content}>
        {/* Esquerda - Lista de Canais */}
        <View style={styles.sidebar}>
          <Text style={styles.sidebarTitle}>Lista de Canais</Text>
          <FlatList
            data={channels}
            keyExtractor={(item) => item.stream_id.toString()}
            renderItem={renderChannelItem}
            showsVerticalScrollIndicator={false}
          />
        </View>

        {/* Direita - Área de Preview / Informações */}
        <View style={styles.previewArea}>
          <Text style={styles.previewTitle}>Selecione um canal</Text>
          <Text style={styles.previewSubtitle}>Clique em um canal na lista à esquerda para assistir</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  loadingContainer: { flex: 1, backgroundColor: colors.background, justifyContent: 'center', alignItems: 'center' },
  loadingText: { color: colors.text, marginTop: 15, fontSize: 16 },
  topMenu: { flexDirection: 'row', paddingVertical: 15, paddingHorizontal: 10, backgroundColor: colors.surface },
  menuItem: { color: colors.textSecondary, fontSize: 16, fontWeight: 'bold', marginHorizontal: 15 },
  menuItemActive: { color: colors.primary },
  content: { flex: 1, flexDirection: 'row' },
  sidebar: { width: '35%', backgroundColor: colors.surface, padding: 10, borderRightWidth: 1, borderRightColor: colors.border },
  sidebarTitle: { color: colors.text, fontSize: 16, fontWeight: 'bold', marginBottom: 10, paddingLeft: 5 },
  channelItem: { flexDirection: 'row', alignItems: 'center', padding: 8, borderBottomWidth: 1, borderBottomColor: colors.border },
  channelLogo: { width: 35, height: 35, marginRight: 10, borderRadius: 5 },
  channelInfo: { flex: 1 },
  channelNumber: { color: colors.textSecondary, fontSize: 12 },
  channelName: { color: colors.text, fontSize: 14, fontWeight: '500' },
  previewArea: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: 20 },
  previewTitle: { color: colors.text, fontSize: 22, fontWeight: 'bold', textAlign: 'center' },
  previewSubtitle: { color: colors.textSecondary, fontSize: 14, textAlign: 'center', marginTop: 10 }
});