package com.example.art;

import com.example.art.user.model.User;
import com.example.art.wallet.model.Wallet;
import com.example.art.wallet.repository.WalletRepository;
import com.example.art.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceUTest {

    @Mock
    private WalletRepository walletRepository;

    @Captor
    private ArgumentCaptor<Wallet> walletArgumentCaptor;

    @InjectMocks
    private WalletService walletService;

    @Test
    void whenGetWalletByUser_thenReturnWallet() {
            User user = new User();
            user.setId(UUID.randomUUID());

            Wallet wallet = new Wallet();
            wallet.setOwner(user);
            wallet.setBalance(new BigDecimal("100.00"));

            when(walletRepository.findByOwner(user)).thenReturn(wallet);

            Wallet result = walletService.getWalletByUser(user);

            assertNotNull(result);
            assertEquals(wallet, result);
            assertEquals(user, result.getOwner());
            assertEquals(new BigDecimal("100.00"), result.getBalance());
        }

    @Test
    void whenNoWalletForUser_thenReturnNull() {
        User user = new User();
        user.setId(UUID.randomUUID());

        when(walletRepository.findByOwner(user)).thenReturn(null);

        Wallet result = walletService.getWalletByUser(user);

        assertNull(result);
    }

    @Test
    void whenCreateWallet_thenSaveWalletToRepository() {
        User user = new User();
        user.setId(UUID.randomUUID());

        walletService.createWallet(user);

        verify(walletRepository, times(1)).save(walletArgumentCaptor.capture());

        Wallet capturedWallet = walletArgumentCaptor.getValue();

        assertEquals(new BigDecimal("100.00"), capturedWallet.getBalance());
        assertEquals("BGN", capturedWallet.getCurrency());
        assertEquals(user, capturedWallet.getOwner());

        assertNotNull(capturedWallet.getCreatedOn());
    }
}

