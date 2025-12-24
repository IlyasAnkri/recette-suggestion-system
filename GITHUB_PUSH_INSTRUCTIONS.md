# 🚀 Instructions pour Pousser sur GitHub

## ⚠️ IMPORTANT: Supprimer l'Ancien Repo GitHub

Avant de pousser, vous devez **supprimer le repo existant** sur GitHub car il contient votre clé API dans l'historique.

### Étape 1: Supprimer le Repo sur GitHub

1. Allez sur https://github.com/IlyasAnkri/recipe-adjuster-microservices
2. Cliquez sur **Settings** (en haut à droite)
3. Scrollez tout en bas
4. Dans la section **"Danger Zone"**, cliquez sur **"Delete this repository"**
5. Tapez le nom du repo pour confirmer: `IlyasAnkri/recipe-adjuster-microservices`
6. Cliquez sur **"I understand the consequences, delete this repository"**

### Étape 2: Créer un Nouveau Repo

1. Allez sur https://github.com
2. Cliquez sur **"New repository"** (bouton vert)
3. Nommez-le: `recipe-adjuster-microservices`
4. **NE PAS** cocher "Initialize with README"
5. Cliquez sur **"Create repository"**

### Étape 3: Pousser le Code Propre

Maintenant, exécutez ces commandes:

```bash
cd c:\Users\hb\suggestionrecette
git remote add origin https://github.com/IlyasAnkri/recipe-adjuster-microservices.git
git branch -M main
git push -u origin main
```

## ✅ Vérifications

Après le push, vérifiez que:
- ❌ Le fichier `.env` n'est PAS visible sur GitHub
- ❌ Les fichiers `.bat` ne sont PAS visibles
- ❌ Les dossiers `infrastructure/`, `web-bundles/`, `scripts/` ne sont PAS visibles
- ✅ Le code source (services/, frontend/) EST visible
- ✅ Le README.md EST visible

## 🔑 Configuration de la Clé API

Votre professeur devra configurer sa propre clé OpenAI:

1. Créer un fichier `.env` à la racine avec:
   ```
   OPENAI_API_KEY=sa-propre-clé
   ```

2. Ou définir la variable d'environnement:
   ```bash
   export OPENAI_API_KEY=sa-propre-clé
   ```

## 📝 Ce Qui a Été Fait

1. ✅ Retiré la clé API de tous les fichiers de configuration
2. ✅ Ajouté `.env` et `*.bat` au `.gitignore`
3. ✅ Créé un nouveau repo Git propre (sans historique contenant la clé)
4. ✅ Configuré les fichiers pour utiliser des variables d'environnement

**Votre projet est maintenant sécurisé et prêt pour GitHub !** 🎉
