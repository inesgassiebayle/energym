import React, { useEffect, useCallback, useState } from 'react';
import axios from 'axios';
import logo from "../Assets/Logo.png";
import './Payment.css';
import spinner from "../Assets/spinner.svg";
import {Link, useParams} from "react-router-dom";

const publicKey = 'APP_USR-3145c0d6-47ae-4279-a428-03d341a11036'; // Reemplaza con tu clave pública de producción

const Payment = () => {
    const [plan, setPlan] = useState('');
    const {username} = useParams();
    const [loading, setLoading] = useState(false);

    const handlePlanChange = (event) => {
        setPlan(event.target.value);
    };

    const handlePayment = useCallback(async () => {
        if (!plan) {
            alert('Por favor selecciona un plan antes de continuar.');
            return;
        }

        const title = plan === 'monthly' ? 'Plan Mensual' : 'Plan Anual';
        const price = plan === 'monthly' ? 10 : 100;

        setLoading(true);

        try {
            console.log('Enviando solicitud de pago...');
            const response = await axios.post(`http://localhost:3333/mp/payment`, {
                title,
                quantity: 1,
                unit_price: price,
                username: username
            });
            console.log('Respuesta del backend:', response.data);

            const { preferenceId } = response.data;

            if (preferenceId) {
                console.log('Preference ID recibido:', preferenceId);
                const mp = new window.MercadoPago(publicKey, {
                    locale: 'es-AR',
                });

                const walletContainer = document.getElementById('wallet_container');
                walletContainer.innerHTML = '';

                mp.checkout({
                    preference: {
                        id: preferenceId,
                    },
                    render: {
                        container: '#wallet_container',
                        label: 'Checkout',
                    },
                });
            } else {
                console.error('No se devolvió un preferenceId válido del backend');
            }
        } catch (error) {
            console.error('Error al crear la preferencia:', error);
        } finally {
            setLoading(false);
        }
    }, [plan]);

    useEffect(() => {
        const script = document.createElement('script');
        script.src = 'https://sdk.mercadopago.com/js/v2';
        script.async = true;
        document.body.appendChild(script);

        script.onload = () => {
            document.getElementById('mp_button').addEventListener('click', handlePayment);
        };

        return () => {
            const mpButton = document.getElementById('mp_button');
            if (mpButton) {
                mpButton.removeEventListener('click', handlePayment);
            }

            const scriptElement = document.querySelector('script[src="https://sdk.mercadopago.com/js/v2"]');
            if (scriptElement) {
                document.body.removeChild(scriptElement);
            }
        };
    }, [handlePayment]);

    return (
        <div className='payment-container'>
            <div className="payment-header">
                <div className="payment-title">
                    <div className="text">Manage Payment</div>
                </div>
                <div className="logo">
                    <img src={logo} alt="logo"/>
                </div>
            </div>
            <div className='payment-actions'>
                <div>
                    <select value={plan} onChange={handlePlanChange} required>
                        <option value="">Select Plan</option>
                        <option value="monthly">Monthly Plan - $10</option>
                        <option value="annual">Yearly Plan - $100</option>
                    </select>
                </div>
            </div>
            <div className='payment-actions'>
                {loading ? (
                    <div className="loading-container">
                        <img src={spinner} alt="Loading..." className="spinner"/>
                    </div>
                ) : (
                    <>
                        <button id="mp_button" className="payment-button">Pay through 'Mercado Pago'</button>
                    </>
                )}
                <div id="wallet_container"></div>
            </div>
        </div>
    );
};

export default Payment;
