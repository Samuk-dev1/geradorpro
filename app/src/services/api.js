import axios from 'axios';

export const loginXtream = async (url, username, password) => {
  const baseUrl = url.replace(/\/$/, ''); // Remove barra final se houver
  const apiUrl = `${baseUrl}/player_api.php?username=${username}&password=${password}`;
  
  try {
    const response = await axios.get(apiUrl, { timeout: 10000 });
    return response.data;
  } catch (error) {
    throw new Error('Falha na conexão com o servidor.');
  }
};

export const getLiveStreams = async (url, username, password) => {
  const apiUrl = `${url}/player_api.php?username=${username}&password=${password}&action=get_live_streams`;
  const response = await axios.get(apiUrl);
  return response.data;
};

export const getStreamUrl = (url, username, password, streamId) => {
  // Formato padrão Xtream Codes para transmissão ao vivo
  return `${url}/live/${username}/${password}/${streamId}.m3u8`;
};