# 📚 Library Manager - Java Swing

Application de gestion de bibliothèque développée en **Java Swing** avec une base de données **MySQL**. Elle permet de gérer les livres, les membres et les emprunts à travers une interface graphique moderne.

---

## 📋 Table des matières

- [Aperçu](#-aperçu)
- [Fonctionnalités](#-fonctionnalités)
- [Technologies utilisées](#-technologies-utilisées)
- [Architecture du projet](#-architecture-du-projet)
- [Base de données](#-base-de-données)
- [Installation](#-installation)
- [Utilisation](#-utilisation)
- [Captures d'écran](#-captures-décran)
- [Structure du code](#-structure-du-code)
- [Concepts Java utilisés](#-concepts-java-utilisés)
- [Améliorations futures](#-améliorations-futures)
- [Auteur](#-auteur)

---

## 🎯 Aperçu

**Library Manager** est une application desktop permettant de :

- Gérer une collection de livres (ajout, modification, suppression)
- Gérer les membres de la bibliothèque
- Gérer les emprunts et retours de livres
- Suivre l'état des livres (disponible, emprunté, réservé)
- Rechercher et filtrer les données

L'application utilise une interface graphique moderne avec une barre latérale de navigation et un système de pages (Dashboard, Books, Members, Borrowings).

---

## ✨ Fonctionnalités

### 📖 Gestion des livres

- ✅ Ajouter un nouveau livre (titre, auteur, catégorie, statut)
- ✅ Modifier les informations d'un livre existant
- ✅ Supprimer un livre
- ✅ Rechercher par titre ou auteur (recherche en temps réel avec debounce)
- ✅ Afficher le statut avec un code couleur (Disponible, Emprunté, Réservé)
- ✅ Filtrer les livres par statut

### 👥 Gestion des membres

- ✅ Ajouter un nouveau membre (nom, prénom, email, téléphone, date d'inscription)
- ✅ Modifier les informations d'un membre
- ✅ Supprimer un membre
- ✅ Rechercher par nom ou prénom
- ✅ Statuts : Actif, Suspendu, Expiré

### 🔖 Gestion des emprunts

- ✅ Créer un emprunt (livre + membre + dates)
- ✅ Modifier un emprunt existant
- ✅ Supprimer un emprunt
- ✅ Marquer un livre comme retourné
- ✅ Détection automatique des retards
- ✅ Filtrage par statut (En cours, Retourné, En retard)
- ✅ Affichage du nom du livre et du membre (via JOIN SQL)

### 📊 Dashboard

- ✅ Vue d'ensemble des statistiques
- ✅ Liste des livres avec leur statut
- ✅ Recherche globale

### 🎨 Interface

- ✅ Navigation par barre latérale (Sidebar)
- ✅ Système de pages avec `CardLayout`
- ✅ Design moderne et épuré
- ✅ Code couleur pour les statuts
- ✅ Champs avec placeholder
- ✅ Dialogues modaux pour l'ajout/modification

---

## 🛠️ Technologies utilisées

| Technologie | Version | Usage |
|-------------|---------|-------|
| **Java** | 21 (OpenJDK) | Langage principal |
| **Java Swing** | — | Interface graphique |
| **MySQL** | 8.x | Base de données |
| **JDBC** | — | Connexion Java ↔ MySQL |
| **Maven** (optionnel) | — | Gestion des dépendances |

### Bibliothèques

- `javax.swing.*` — Composants graphiques
- `java.sql.*` — Connexion BD
- `java.time.*` — Gestion des dates (`LocalDate`)
- `java.util.function.*` — Programmation fonctionnelle (`Consumer`, `Function`, `Predicate`)

---

## 🏗️ Architecture du projet
