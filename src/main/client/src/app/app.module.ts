import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { MarkdownModule, MarkedOptions, MarkedRenderer } from 'ngx-markdown';

import { AppComponent } from './app.component';
import { VersionComponent } from './version/version.component';

@NgModule({
    declarations: [
        AppComponent,
        VersionComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        MarkdownModule.forRoot({
            loader: HttpClientModule,
            markedOptions: {
                provide: MarkedOptions,
                useFactory: markedOptionsFactory,
            },
        })
    ],
    providers: [],
    bootstrap: [AppComponent]
})

export class AppModule { }

/**
 * Markdown rendering options and overwrites.
 */
export function markedOptionsFactory(): MarkedOptions {
    const renderer = new MarkedRenderer();


    renderer.table = (header: string, body: string) => {
        if (body) body = '<tbody>' + body + '</tbody>';

        return '<table class="table table-sm table-striped table-bordered">'
          + '<thead>'
          + header
          + '</thead>'
          + body
          + '</table>';
    }

    return {
        renderer: renderer,
        gfm: true,
        tables: true,
        breaks: false,
        pedantic: false,
        sanitize: false,
        smartLists: true,
        smartypants: false,
    };
}
