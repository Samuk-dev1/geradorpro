import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import colors from '../styles/colors';
import { loginXtream } from '../services/api';

export default function LoginScreen({ navigation }) {
  const [url, setUrl] = useState('');
  const [user, setUser] = useState('');
  const [pass, setPass] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    if (!url || !user || !pass) {
      Alert.alert('Erro', 'Preencha todos os campos.');
      return;
    }

    setLoading(true);
    try {
      const data = await loginXtream(url, user, pass);
      
      if (data && data.user_info) {
        if (data.user_info.status === 'Active') {
          const baseUrl = url.replace(/\/$/, '');
          await AsyncStorage.setItem('@iptv_data', JSON.stringify({ url: baseUrl, user, pass }));
          navigation.replace('Home');
        } else {
          Alert.alert('Atenção', 'Sua conta está expirada ou inativa.');
        }
      } else {
        Alert.alert('Erro', 'Resposta inválida do servidor.');
      }
    } catch (error) {
      Alert.alert('Erro', error.message || 'Verifique a URL e sua internet.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.logo}>MEU IPTV</Text>
      <Text style={styles.subtitle}>Conecte-se ao seu servidor</Text>

      <TextInput style={styles.input} placeholder="URL do Servidor (http://...)" placeholderTextColor={colors.textSecondary} value={url} onChangeText={setUrl} autoCapitalize="none" keyboardType="url" />
      <TextInput style={styles.input} placeholder="Usuário" placeholderTextColor={colors.textSecondary} value={user} onChangeText={setUser} autoCapitalize="none" />
      <TextInput style={styles.input} placeholder="Senha" placeholderTextColor={colors.textSecondary} value={pass} onChangeText={setPass} secureTextEntry />

      <TouchableOpacity style={styles.button} onPress={handleLogin} disabled={loading}>
        {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>ENTRAR</Text>}
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background, justifyContent: 'center', padding: 25 },
  logo: { fontSize: 36, color: colors.primary, textAlign: 'center', fontWeight: 'bold', marginBottom: 10 },
  subtitle: { fontSize: 16, color: colors.textSecondary, textAlign: 'center', marginBottom: 40 },
  input: { backgroundColor: colors.surface, color: colors.text, padding: 15, borderRadius: 8, marginBottom: 15, fontSize: 16, borderWidth: 1, borderColor: colors.border },
  button: { backgroundColor: colors.primary, padding: 16, borderRadius: 8, alignItems: 'center', marginTop: 10 },
  buttonText: { color: '#fff', fontSize: 18, fontWeight: 'bold' }
});