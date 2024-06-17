import React, { useEffect, useCallback } from 'react';
import axios from 'axios';

const publicKey = 'APP_USR-d9b3a458-a330-4f0e-947b-ce234a7aba10'; // Reemplaza con tu clave pública de producción

const Pago = () => {
    const handlePayment = useCallback(async () => {
        try {
            console.log('Enviando solicitud de pago...');
            const response = await axios.post('http://localhost:3333/api/mp', {
                title: 'manzana',
                quantity: 1,
                unit_price: 1,
            });
            console.log('Respuesta del backend:', response.data);

            const { preferenceId } = response.data;

            if (preferenceId) {
                console.log('Preference ID recibido:', preferenceId);
                const mp = new window.MercadoPago(publicKey, {
                    locale: 'es-AR',
                });

                const walletContainer = document.getElementById('wallet_container');
                walletContainer.innerHTML = ''; // Limpia el contenedor antes de añadir un nuevo botón

                mp.checkout({
                    preference: {
                        id: preferenceId,
                    },
                    render: {
                        container: '#wallet_container',
                        label: 'Pagar',
                    },
                });
            } else {
                console.error('No se devolvió un preferenceId válido del backend');
            }
        } catch (error) {
            console.error('Error al crear la preferencia:', error);
        }
    }, []); // La dependencia vacía asegura que useCallback crea la función una sola vez

    useEffect(() => {
        const script = document.createElement('script');
        script.src = 'https://sdk.mercadopago.com/js/v2';
        script.async = true;
        document.body.appendChild(script);

        script.onload = () => {
            document.getElementById('mp_button').addEventListener('click', handlePayment);
        };

        return () => {
            // Remueve el evento y el script al desmontar el componente
            const mpButton = document.getElementById('mp_button');
            if (mpButton) {
                mpButton.removeEventListener('click', handlePayment);
            }

            const scriptElement = document.querySelector('script[src="https://sdk.mercadopago.com/js/v2"]');
            if (scriptElement) {
                document.body.removeChild(scriptElement);
            }
        };
    }, [handlePayment]); // handlePayment como dependencia asegura que la función es consistente

    return (
        <div>
            <h2>Compra manzana</h2>
            <button id="mp_button">Comprar</button>
            <div id="wallet_container"></div>
        </div>
    );
};

export default Pago;
