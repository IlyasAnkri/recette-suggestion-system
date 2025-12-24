import { Injectable, ApplicationRef } from '@angular/core';
import { SwUpdate, VersionReadyEvent } from '@angular/service-worker';
import { filter, first, interval, concat } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SwUpdateService {
  updateAvailable = false;

  constructor(
    private swUpdate: SwUpdate,
    private appRef: ApplicationRef
  ) {}

  init(): void {
    if (!this.swUpdate.isEnabled) {
      console.log('Service Worker is not enabled');
      return;
    }

    // Check for updates when app stabilizes
    const appIsStable$ = this.appRef.isStable.pipe(
      first(isStable => isStable === true)
    );
    
    const everySixHours$ = interval(6 * 60 * 60 * 1000);
    const everySixHoursOnceAppIsStable$ = concat(appIsStable$, everySixHours$);

    everySixHoursOnceAppIsStable$.subscribe(async () => {
      try {
        const updateFound = await this.swUpdate.checkForUpdate();
        console.log(updateFound ? 'A new version is available.' : 'Already on the latest version.');
      } catch (err) {
        console.error('Failed to check for updates:', err);
      }
    });

    // Listen for version updates
    this.swUpdate.versionUpdates
      .pipe(
        filter((evt): evt is VersionReadyEvent => evt.type === 'VERSION_READY')
      )
      .subscribe(evt => {
        console.log('New version available:', evt.latestVersion);
        this.updateAvailable = true;
        this.promptUser();
      });

    // Handle unrecoverable state
    this.swUpdate.unrecoverable.subscribe(event => {
      console.error('Service Worker in unrecoverable state:', event.reason);
      this.notifyUnrecoverableState(event.reason);
    });
  }

  private promptUser(): void {
    // This would typically show a snackbar or dialog
    // For now, we'll just log and auto-update after a delay
    console.log('Update available. Will reload in 5 seconds...');
    
    setTimeout(() => {
      this.activateUpdate();
    }, 5000);
  }

  activateUpdate(): void {
    if (!this.swUpdate.isEnabled) {
      return;
    }

    this.swUpdate.activateUpdate().then(() => {
      console.log('Update activated. Reloading...');
      document.location.reload();
    }).catch(err => {
      console.error('Failed to activate update:', err);
    });
  }

  private notifyUnrecoverableState(reason: string): void {
    console.error('App is in an unrecoverable state:', reason);
    // In production, you might want to show a message to the user
    // and provide a button to reload the page
  }

  checkForUpdate(): void {
    if (!this.swUpdate.isEnabled) {
      return;
    }

    this.swUpdate.checkForUpdate().then(updateFound => {
      if (updateFound) {
        console.log('Update found!');
      } else {
        console.log('No update available');
      }
    }).catch(err => {
      console.error('Failed to check for update:', err);
    });
  }
}
