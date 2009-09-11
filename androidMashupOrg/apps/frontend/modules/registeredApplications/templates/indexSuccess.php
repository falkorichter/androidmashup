<h1><a name="top">Registered Applications:</a></h1>
<ul>
<?
foreach ( $applications as $application ) {
	$developer = $application->getDeveloper();?>
	<h2><?= $application->getName()?></h2>
	
	<? if(strlen($application->getIcon()) != 0){?>
	<img src="<?= $application->getIcon()?>"/ style="float:right;">
	<? } ?>
	
	
	<? if(strlen($application->getUrl()) != 0){?>
	<p><strong>URL: <a href="<?=  $application->getUrl();?>"><?=  $application->getUrl();?></a></strong>	</p>
	<? } ?>
	
	<? if(strlen($application->getApkUrl()) != 0){?>
	<p><strong><a href="<?=  $application->getApkUrl();?>">direkt link to the apk file</a></strong>	</p>
	<? } ?>
	
	
	
	<p><strong>Description:</strong> </p>
	<p><?= $application->getDescription();?></p>

	
	<? $intents = $application->getMashupIntents();
	if(count($intents) > 0){?>
		<h3>Supported Mashup Intents:</h3>
		
		<ul>
		<? foreach ( $intents as $intent ) {?>
			<li><a href="/mashupIntents#<?=$intent->getPrimaryKey()?>"><?=$intent->getAction()?></a></li>
			<? }?>
		</ul>
	<? }?>
	
	<? $intents = $application->getPrivateIntents();
	if(count($intents) > 0){?>
		<h3>Other Intents:</h3>
		<ul>
		<? foreach ( $intents as $intent ) {?>
				<li><?=$intent->getAction()?></li>
			<? }?>
		</ul>
	<? }?>
<?	}?>





