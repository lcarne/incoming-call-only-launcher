# Incoming Call Only Launcher – Launcher Android Minimaliste & Sécurisé

[![en](https://img.shields.io/badge/lang-en-red.svg)](README.md)
[![fr](https://img.shields.io/badge/lang-fr-blue.svg)](README.fr.md)

<p>
  <img src="images/app_icon.svg" alt="Icône de l'application" width="160"/>
</p>

Incoming Call Only Launcher est un **launcher Android open-source et minimaliste conçu pour les seniors et les personnes vulnérables**.

Il transforme un appareil Android en un **téléphone de réception uniquement**, permettant les appels entrants exclusivement de contacts de confiance tout en désactivant complètement les appels sortants et toutes les fonctionnalités système non essentielles.

L'objectif est d'offrir une **expérience calme, sûre et sans confusion**, tout en donnant aux aidants et aux membres de la famille un contrôle total sur l'appareil.

**Conçu pour :** Seniors • Établissements de soins • Hôpitaux • Aidants • Appareils Kiosque

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Android](https://img.shields.io/badge/platform-Android-green.svg)

---

## Objectif

Incoming Call Only Launcher verrouille l'utilisateur dans une **interface à usage unique** axée sur la sécurité et la clarté.

L'utilisateur peut :
- Voir la date et l'heure actuelles avec un **affichage large et à fort contraste**
- Recevoir **uniquement des appels entrants** d'une liste de contacts de confiance

Tout le reste est intentionnellement caché ou restreint :
- Pas d'appels sortants
- Pas de notifications
- Pas d'applications de messagerie
- Pas de paramètres système ou de navigation
- Pas d'interactions accidentelles

Cette conception minimise la confusion et empêche une mauvaise utilisation.

**Les cas d'utilisation typiques incluent :**
- Les utilisateurs âgés qui ne devraient pas passer d'appels accidentels ou d'urgence
- Les personnes atteintes de la maladie d'Alzheimer ou de troubles cognitifs
- Les patients dans des établissements de soins ou des hôpitaux
- Les enfants ou les personnes vulnérables utilisant un appareil dédié
- Les situations où les aidants ont besoin d'un contrôle total sur qui peut appeler l'appareil

---

## Ce que cette application est (et n'est pas)

**Incoming Call Only Launcher est intentionnellement limité par conception.**

✔ C'est :
- Une interface de téléphone en réception uniquement
- Un launcher Android axé sur la sécurité
- Un environnement contrôlé pour les aidants

✖ Ce n'est PAS :
- Une application téléphonique standard
- Un composeur (dialer)
- Une application de messagerie
- Un launcher généraliste

---

## Fonctionnalités Clés

- **Interface Utilisateur Centrée sur les Seniors**
  Grande horloge numérique, date complète et thème à fort contraste. Écrans d'appel repensés avec un texte extra-large, des boutons d'action vibrants et un style visuel rassurant.

- **Routage Audio Intelligent**
  Les appels entrants commencent par défaut sur **haut-parleur**, aidant les utilisateurs ayant des problèmes d'audition ou de dextérité. Basculez entre le haut-parleur et l'écouteur à l'aide de gros boutons clairs.

- **Gestion des Appels Sécurisée**
  - Les appels entrants ne sonnent que pour les contacts.
  - Les appelants inconnus sont **automatiquement réduits au silence ou rejetés**.
  - **Sécurité à 2 Tapes** : Les actions Raccrocher et Refuser nécessitent deux tapes pour éviter la fin accidentelle de l'appel.

- **Support Kiosque / Propriétaire de l'Appareil**
  Lorsqu'elle est définie comme Propriétaire de l'Appareil, l'application peut :
  - Désactiver la barre d'état
  - Bloquer les gestes de navigation du système
  - Empêcher de quitter le launcher

- **Interface Administrateur Protégée**
  Un écran d'administration caché permet aux aidants de gérer les contacts et le comportement de l'appareil sans risque d'accès accidentel.

- **Accès PIN Simple**
  L'accès administrateur est protégé par un code PIN (par défaut : `1234`) pour un accès rapide et contrôlé par l'aidant.

- **Historique des Appels**
  Voir un journal détaillé des appels entrants, manqués et rejetés avec la durée et l'horodatage.

- **Sauvegarde et Restauration Locale**
  Exportez facilement votre liste de contacts vers un fichier JSON et restaurez-la sur un autre appareil ou après une réinitialisation.

---

## Confidentialité et Données

Incoming Call Only Launcher ne **collecte, ne stocke ni ne transmet aucune donnée personnelle**.

- Pas d'analytique
- Pas de suivi
- Pas de services cloud
- Pas d'intégrations tierces

Tous les contacts, paramètres et historiques d'appels sont stockés **localement sur l'appareil uniquement**.

---

## Stack Technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose (Material 3)
- **Typographie** : [Famille de polices Inter](https://rsms.me/inter/) (Licence SIL Open Font)
- **Architecture** : MVVM + Hilt
- **Stockage** : Base de données Room
- **Sécurité** :
  - `DevicePolicyManager` (Mode Propriétaire de l'Appareil / Kiosque)
  - `CallScreeningService` (filtrage des appels entrants)

Voir [ATTRIBUTIONS.md](ATTRIBUTIONS.md) pour les licences tierces.

---

## Captures d'écran

> Interface propre et à fort contraste conçue pour les personnes âgées et vulnérables.

### Accueil et Appel Entrant

<table>
  <tr>
    <td align="center">
      <img src="images/home.png" width="320" alt="Écran d'accueil"/><br/>
      <strong>Écran d'accueil</strong>
    </td>
    <td align="center">
      <img src="images/incoming_call.png" width="320" alt="Appel entrant"/><br/>
      <strong>Appel entrant</strong>
    </td>
    <td align="center">
      <img src="images/ongoing_call.png" width="280" alt="Appel en cours"/><br/>
      <strong>Appel en cours</strong>
    </td>
  </tr>
</table>

---

### Administration et Gestion des Contacts

<table>
  <tr>
    <td align="center">
      <img src="images/admin.png" width="320" alt="Entrée PIN Admin"/><br/>
      <strong>Admin / Entrée PIN</strong>
    </td>
    <td align="center">
      <img src="images/contacts.png" width="320" alt="Contacts"/><br/>
      <strong>Gestion des contacts</strong>
    </td>
    <td align="center">
      <img src="images/contacts_add.png" width="320" alt="Ajouter un contact"/><br/>
      <strong>Ajouter un contact</strong>
    </td>
  </tr>
</table>

---

### Paramètres

<table>
  <tr>
    <td align="center">
      <img src="images/settings_1.png" width="320" alt="Déverrouillage, Contenu et Système"/><br/>
      <strong>Déverrouillage, Contenu et Système</strong>
    </td>
    <td align="center">
      <img src="images/settings_2.png" width="320" alt="Audio, Affichage et Alimentation"/><br/>
      <strong>Audio, Affichage et Alimentation</strong>
    </td>
    <td align="center">
      <img src="images/settings_3.png" width="320" alt="Affichage et Localisation"/><br/>
      <strong>Affichage et Localisation</strong>
    </td>
    <td align="center">
      <img src="images/settings_4.png" width="320" alt="Gestion des Données et Support"/><br/>
      <strong>Gestion des Données et Support</strong>
    </td>
  </tr>
</table>

---

### États Supplémentaires

<table>
  <tr>
    <td align="center">
      <img src="images/home_ring_off.png" width="280" alt="Sonnerie désactivée"/><br/>
      <strong>Sonnerie désactivée</strong>
    </td>
    <td align="center">
      <img src="images/home_night.png" width="280" alt="Mode nuit"/><br/>
      <strong>Mode nuit</strong>
    </td>
    <td align="center">
      <img src="images/dim_mode.png" width="280" alt="Mode sombre"/><br/>
      <strong>Mode sombre</strong>
    </td>
  </tr>
</table>

---

## Installation et Configuration

### Option 1 – Télécharger l'APK pré-construit (recommandé)

Vous pouvez télécharger un APK prêt à installer depuis les Releases GitHub :

➡️ https://github.com/lcarne/call-only-launcher/releases

Chaque version inclut :
- Un APK signé
- Les notes de version

**Étapes :**
1. Téléchargez le fichier `.apk` depuis la page des Releases.
2. Copiez-le sur l'appareil Android cible.
3. Autorisez l'installation depuis des sources inconnues si on vous le demande.
4. Installez l'APK.

---

### Option 2 – Appuyer depuis la source

1. Clonez ce dépôt.
2. Ouvrez le projet dans Android Studio.
3. Construisez et installez l'APK sur l'appareil cible.

---

## Définir comme Launcher par Défaut

Après l'installation :
1. Appuyez sur le bouton **Accueil**.
2. Sélectionnez **Incoming Call Only Launcher**.
3. Choisissez **Toujours** pour en faire le launcher par défaut.

---

## Activer le Vrai Mode Kiosque (Propriétaire de l'Appareil)

Pour un verrouillage complet (désactiver la barre d'état, la navigation, les gestes système), définissez l'application comme **Propriétaire de l'Appareil**.

⚠️ **Attention**
Cette action est irréversible sans accès ADB.

### Prérequis
- Supprimer les comptes Google de l'appareil (recommandé)
- Activer le **débogage USB** dans les Options pour les développeurs

### Commande ADB

```bash
adb shell dpm set-device-owner com.incomingcallonly.launcher/.receivers.IncomingCallOnlyAdminReceiver
```

Si réussi, le launcher sera épinglé et la barre d'état/navigation seront désactivées selon la politique de l'appareil.

## Accès Administrateur (Comment ouvrir l'écran Admin)

L'interface Admin est intentionnellement cachée pour éviter tout accès accidentel par l'utilisateur final.

- Sur l'écran d'accueil, **appuyez rapidement 15 fois sur la zone date/heure** pour ouvrir le point d'entrée Admin.
- Entrez le code PIN par défaut : `1234` (code PIN par défaut, peut être modifié dans les Paramètres Admin)

Depuis l'interface Admin, vous pouvez :
- **Gérer les Contacts** : Ajouter, modifier ou supprimer des contacts de confiance.
- **Voir l'Historique des Appels** : Vérifier l'activité récente, y compris les appels bloqués.
- **Gestion des Données** :
  - **Exporter/Importer les Contacts** : Sauvegarder votre liste de confiance dans un fichier JSON.
  - **Réinitialiser les Données** : Effacer l'historique des appels ou réinitialiser les paramètres de l'application.
- **Écran et Alimentation** : Configurer le comportement en fonction de l'état de l'alimentation (Branché vs Sur Batterie) :
  - **Éteint** : Délai d'attente standard d'Android.
  - **Sombre** : L'écran reste allumé avec une luminosité réduite, n'affichant que l'horloge.
  - **Éveillé** : L'écran reste allumé avec une luminosité normale.
- **Personnalisation** : Configurer le mode Nuit, la couleur de l'Horloge, le volume de la Sonnerie.
- **Contrôle Système** : Désépingler/déverrouiller temporairement l'appareil ou définir comme Launcher par Défaut.

## Déverrouillage d'Urgence / Supprimer le Propriétaire de l'Appareil

Si vous ne pouvez pas accéder au bouton de déverrouillage Admin, supprimez le Propriétaire de l'Appareil via ADB :

```bash
adb shell dpm remove-active-admin com.incomingcallonly.launcher/.receivers.IncomingCallOnlyAdminReceiver
```

## Notes et Détails d'Implémentation

- Le récepteur Admin est `com.incomingcallonly.launcher.receivers.IncomingCallOnlyAdminReceiver` et est déclaré dans le manifeste avec la permission `BIND_DEVICE_ADMIN`.
- Le comportement Kiosque (verrouillage des tâches, désactivation de la barre d'état) est contrôlé via `DevicePolicyManager` dans `MainActivity`.

## Contribuer

Les contributions et corrections sont les bienvenues. Veuillez ouvrir des problèmes (issues) ou des demandes de tirage (pull requests) pour des demandes de fonctionnalités, des corrections ou des mises à jour de documentation.

## Licence

Ce projet est sous licence MIT, voir le fichier [LICENSE](LICENSE).

## Crédits

- [Famille de polices Inter](https://rsms.me/inter/) (Licence SIL Open Font)
- [Material You](https://material.io/you)

---

*100% gratuit, 100% open source, fait avec ❤️ pour ma grand-mère*
